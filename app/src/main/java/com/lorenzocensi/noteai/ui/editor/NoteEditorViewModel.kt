package com.lorenzocensi.noteai.ui.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lorenzocensi.noteai.data.repository.ConnectionRepository
import com.lorenzocensi.noteai.data.repository.NoteRepository
import com.lorenzocensi.noteai.domain.model.Note
import com.lorenzocensi.noteai.domain.model.SuggestedLink
import com.lorenzocensi.noteai.domain.usecase.SaveNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val noteRepo: NoteRepository,
    private val connectionRepo: ConnectionRepository,
    private val saveNote: SaveNoteUseCase
) : ViewModel() {

    private val noteId: String = checkNotNull(savedStateHandle["noteId"])

    val note: StateFlow<Note?> = noteRepo.observeById(noteId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val links: StateFlow<List<SuggestedLink>> = connectionRepo.observeForNote(noteId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

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
