package com.lorenzocensi.noteai.domain.usecase

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.lorenzocensi.noteai.data.repository.NoteRepository
import com.lorenzocensi.noteai.domain.model.Note
import com.lorenzocensi.noteai.work.ConnectionDiscoveryWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SaveNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository,
    private val workManager: WorkManager
) {
    suspend operator fun invoke(note: Note, runImmediately: Boolean = false) {
        noteRepository.upsert(note)
        enqueueDiscovery(note.id, immediate = runImmediately)
    }

    fun enqueueDiscovery(noteId: String, immediate: Boolean = false) {
        val req = OneTimeWorkRequestBuilder<ConnectionDiscoveryWorker>()
            .setInputData(workDataOf(ConnectionDiscoveryWorker.KEY_NOTE_ID to noteId))
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .apply { if (!immediate) setInitialDelay(DEBOUNCE_SECONDS, TimeUnit.SECONDS) }
            .addTag(TAG)
            .build()
        workManager.enqueueUniqueWork(
            "ai-link-$noteId",
            ExistingWorkPolicy.REPLACE,
            req
        )
    }

    companion object {
        const val TAG = "ai-link"
        const val DEBOUNCE_SECONDS = 10L
    }
}
