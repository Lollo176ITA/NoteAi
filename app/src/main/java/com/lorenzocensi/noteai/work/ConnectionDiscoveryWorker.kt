package com.lorenzocensi.noteai.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.lorenzocensi.noteai.data.db.entity.ConnectionEntity
import com.lorenzocensi.noteai.data.remote.MissingApiKeyException
import com.lorenzocensi.noteai.data.remote.NimApi
import com.lorenzocensi.noteai.data.remote.dto.ChatRequest
import com.lorenzocensi.noteai.data.remote.dto.LinkFinderResult
import com.lorenzocensi.noteai.data.remote.dto.ResponseFormat
import com.lorenzocensi.noteai.data.remote.prompt.LinkFinderPrompt
import com.lorenzocensi.noteai.data.repository.ConnectionRepository
import com.lorenzocensi.noteai.data.repository.NoteRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.serialization.json.Json
import java.io.IOException

@HiltWorker
class ConnectionDiscoveryWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val noteRepository: NoteRepository,
    private val connectionRepository: ConnectionRepository,
    private val nimApi: NimApi,
    private val json: Json
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val noteId = inputData.getString(KEY_NOTE_ID) ?: return Result.failure()
        val note = noteRepository.getById(noteId) ?: return Result.success()
        if (note.title.isBlank() && note.body.isBlank()) {
            connectionRepository.replaceForNote(noteId, emptyList())
            return Result.success(workDataOf(KEY_LINK_COUNT to 0))
        }

        return try {
            val others = noteRepository.snippetsForProject(note.projectId, noteId)
            if (others.isEmpty()) {
                connectionRepository.replaceForNote(noteId, emptyList())
                return Result.success(workDataOf(KEY_LINK_COUNT to 0))
            }
            val noteEntity = com.lorenzocensi.noteai.data.db.entity.NoteEntity(
                id = note.id,
                projectId = note.projectId,
                title = note.title,
                body = note.body,
                createdAt = note.createdAt,
                updatedAt = note.updatedAt
            )
            val messages = LinkFinderPrompt.build(noteEntity, others, json)
            val request = ChatRequest(
                model = NimApi.MODEL_NEMOTRON_3_SUPER,
                messages = messages,
                temperature = 0.2,
                responseFormat = ResponseFormat(type = "json_object"),
                maxTokens = 2048
            )
            val response = nimApi.chatCompletions(request)
            if (!response.isSuccessful) {
                Log.w(TAG, "NIM HTTP ${response.code()}")
                return if (response.code() in listOf(401, 403)) {
                    Result.failure(workDataOf(KEY_REASON to REASON_AUTH))
                } else {
                    Result.retry()
                }
            }
            val content = response.body()?.choices?.firstOrNull()?.message?.content
                ?: return Result.success(workDataOf(KEY_LINK_COUNT to 0))
            val cleaned = LinkFinderPrompt.stripFences(content)
            val parsed = runCatching { json.decodeFromString(LinkFinderResult.serializer(), cleaned) }
                .getOrElse {
                    Log.w(TAG, "Parse fail, lascio collegamenti precedenti", it)
                    return Result.success(workDataOf(KEY_LINK_COUNT to 0))
                }

            val now = System.currentTimeMillis()
            val validIds = others.map { it.id }.toSet()
            val mapped = parsed.links
                .asSequence()
                .filter { it.targetId in validIds && it.targetId != note.id }
                .distinctBy { it.targetId }
                .take(10)
                .map { ConnectionEntity.create(note.id, it.targetId, it.reason, it.score, now) }
                .toList()

            connectionRepository.replaceForNote(note.id, mapped)
            Result.success(workDataOf(KEY_LINK_COUNT to mapped.size))
        } catch (mk: MissingApiKeyException) {
            Log.w(TAG, "Chiave NIM assente", mk)
            Result.failure(workDataOf(KEY_REASON to REASON_MISSING_KEY))
        } catch (io: IOException) {
            Log.w(TAG, "I/O verso NIM", io)
            if (runAttemptCount >= MAX_ATTEMPTS) {
                Result.failure(workDataOf(KEY_REASON to REASON_NETWORK))
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Errore inatteso", e)
            Result.failure(workDataOf(KEY_REASON to REASON_UNKNOWN))
        }
    }

    companion object {
        const val KEY_NOTE_ID = "noteId"
        const val KEY_REASON = "reason"
        const val KEY_LINK_COUNT = "linkCount"
        const val REASON_MISSING_KEY = "missing_key"
        const val REASON_AUTH = "auth"
        const val REASON_NETWORK = "network"
        const val REASON_UNKNOWN = "unknown"
        const val TAG = "ai-link"
        const val MAX_ATTEMPTS = 3
    }
}
