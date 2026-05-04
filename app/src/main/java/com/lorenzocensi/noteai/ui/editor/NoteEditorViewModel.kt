package com.lorenzocensi.noteai.ui.editor

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.lorenzocensi.noteai.data.repository.ConnectionRepository
import com.lorenzocensi.noteai.data.repository.NoteRepository
import com.lorenzocensi.noteai.domain.model.AiStatus
import com.lorenzocensi.noteai.domain.model.Note
import com.lorenzocensi.noteai.domain.model.SuggestedLink
import com.lorenzocensi.noteai.domain.usecase.SaveNoteUseCase
import com.lorenzocensi.noteai.work.ConnectionDiscoveryWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val noteRepo: NoteRepository,
    private val connectionRepo: ConnectionRepository,
    private val saveNote: SaveNoteUseCase,
    workManager: WorkManager
) : ViewModel() {

    private val noteId: String = checkNotNull(savedStateHandle["noteId"])

    val note: StateFlow<Note?> = noteRepo.observeById(noteId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val links: StateFlow<List<SuggestedLink>> = connectionRepo.observeForNote(noteId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val aiStatus: StateFlow<AiStatus> = workManager
        .getWorkInfosForUniqueWorkFlow("ai-link-$noteId")
        .flatMapLatest { infos ->
            val info = infos.maxByOrNull { it.runAttemptCount + it.state.ordinal }
                ?: return@flatMapLatest flowOf(AiStatus.Idle)
            when (info.state) {
                WorkInfo.State.ENQUEUED, WorkInfo.State.BLOCKED -> flowOf(AiStatus.Pending(0))
                WorkInfo.State.RUNNING -> flowOf(AiStatus.Running)
                WorkInfo.State.SUCCEEDED -> flowOf(
                    AiStatus.Done(info.outputData.getInt(ConnectionDiscoveryWorker.KEY_LINK_COUNT, 0))
                )
                WorkInfo.State.FAILED -> {
                    val reason = info.outputData.getString(ConnectionDiscoveryWorker.KEY_REASON)
                    flowOf(
                        when (reason) {
                            ConnectionDiscoveryWorker.REASON_MISSING_KEY -> AiStatus.MissingApiKey
                            else -> AiStatus.Error(reason ?: ConnectionDiscoveryWorker.REASON_UNKNOWN)
                        }
                    )
                }
                WorkInfo.State.CANCELLED -> flowOf(AiStatus.Idle)
            }
        }
        .catch { t ->
            Log.e(TAG, "aiStatus flow failed", t)
            emit(AiStatus.Idle)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AiStatus.Idle)

    fun update(title: String, body: String) {
        viewModelScope.launch {
            try {
                val existing = noteRepo.getById(noteId) ?: return@launch
                val updated = existing.copy(
                    title = title,
                    body = body,
                    updatedAt = System.currentTimeMillis()
                )
                saveNote(updated, runImmediately = false)
            } catch (t: Throwable) {
                Log.e(TAG, "update failed", t)
            }
        }
    }

    fun recomputeNow() {
        try {
            saveNote.enqueueDiscovery(noteId, immediate = true)
        } catch (t: Throwable) {
            Log.e(TAG, "recomputeNow failed", t)
        }
    }

    suspend fun lookupLinkedNoteTitle(otherId: String): String? =
        runCatching { noteRepo.getById(otherId)?.title }.getOrNull()

    private companion object {
        const val TAG = "NoteEditorVM"
    }
}
