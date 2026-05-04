package com.lorenzocensi.noteai.ui.editor

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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
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
                WorkInfo.State.ENQUEUED, WorkInfo.State.BLOCKED -> flow {
                    while (true) {
                        val now = System.currentTimeMillis()
                        val target = info.nextScheduleTimeMillis
                        val secondsLeft = ((target - now) / 1000L)
                            .coerceAtLeast(0L)
                            .coerceAtMost(60L)
                            .toInt()
                        emit(AiStatus.Pending(secondsLeft))
                        if (secondsLeft <= 0) break
                        delay(1000)
                    }
                }
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
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AiStatus.Idle)

    fun update(title: String, body: String) {
        viewModelScope.launch {
            val existing = noteRepo.getById(noteId) ?: return@launch
            val updated = existing.copy(
                title = title,
                body = body,
                updatedAt = System.currentTimeMillis()
            )
            saveNote(updated, runImmediately = false)
        }
    }

    fun recomputeNow() {
        saveNote.enqueueDiscovery(noteId, immediate = true)
    }

    suspend fun lookupLinkedNoteTitle(otherId: String): String? =
        noteRepo.getById(otherId)?.title
}
