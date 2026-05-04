package com.lorenzocensi.noteainext.ui.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lorenzocensi.noteainext.data.repository.NoteLinkRepository
import com.lorenzocensi.noteainext.data.repository.NoteRepository
import com.lorenzocensi.noteainext.domain.model.Note
import com.lorenzocensi.noteainext.domain.model.NoteLinkPreview
import com.lorenzocensi.noteainext.domain.model.SaveStatus
import com.lorenzocensi.noteainext.domain.usecase.AutosaveController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditorDraft(val title: String, val body: String)

data class EditorUiState(
    val title: String = "",
    val body: String = "",
    val saveStatus: SaveStatus = SaveStatus.Saved,
    val links: List<NoteLinkPreview> = emptyList()
)

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository,
    linkRepository: NoteLinkRepository
) : ViewModel() {
    private val noteId: String = checkNotNull(savedStateHandle["noteId"])
    private val title = MutableStateFlow("")
    private val body = MutableStateFlow("")
    private val autosave = AutosaveController<EditorDraft>(viewModelScope) { draft ->
        val current = noteRepository.getById(noteId)
        if (current != null) {
            noteRepository.upsert(
                current.copy(
                    title = draft.title,
                    body = draft.body,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }
    private var isLoaded = false

    private val links: StateFlow<List<NoteLinkPreview>> = combine(
        linkRepository.observeForNote(noteId),
        noteRepository.observeAll()
    ) { noteLinks, notes ->
        val titles = notes.associate { it.id to it.displayTitle() }
        noteLinks.map { link ->
            NoteLinkPreview(
                link = link,
                noteATitle = titles[link.noteAId] ?: "Nota eliminata",
                noteBTitle = titles[link.noteBId] ?: "Nota eliminata"
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val uiState: StateFlow<EditorUiState> = combine(title, body, autosave.status, links) { t, b, status, linkItems ->
        EditorUiState(title = t, body = b, saveStatus = status, links = linkItems)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EditorUiState())

    init {
        viewModelScope.launch {
            val note = noteRepository.getById(noteId)
            title.value = note?.title.orEmpty()
            body.value = note?.body.orEmpty()
            isLoaded = true
        }
    }

    fun onTitleChange(value: String) {
        title.value = value
        scheduleAutosave()
    }

    fun onBodyChange(value: String) {
        body.value = value
        scheduleAutosave()
    }

    private fun scheduleAutosave() {
        if (!isLoaded) return
        autosave.schedule(EditorDraft(title.value, body.value))
    }

    override fun onCleared() {
        autosave.cancel()
        super.onCleared()
    }

    private fun Note.displayTitle(): String =
        title.ifBlank { body.lineSequence().firstOrNull().orEmpty() }.ifBlank { "Senza titolo" }
}
