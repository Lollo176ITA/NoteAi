package com.lorenzocensi.noteainext.ui.links

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lorenzocensi.noteainext.data.repository.NoteLinkRepository
import com.lorenzocensi.noteainext.data.repository.NoteRepository
import com.lorenzocensi.noteainext.domain.model.Note
import com.lorenzocensi.noteainext.domain.model.NoteLink
import com.lorenzocensi.noteainext.domain.model.NoteLinkPreview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LinksUiState(
    val notes: List<Note> = emptyList(),
    val links: List<NoteLinkPreview> = emptyList()
)

@HiltViewModel
class LinksViewModel @Inject constructor(
    noteRepository: NoteRepository,
    private val linkRepository: NoteLinkRepository
) : ViewModel() {
    val uiState: StateFlow<LinksUiState> = combine(
        noteRepository.observeAll(),
        linkRepository.observePreviews()
    ) { notes, links ->
        LinksUiState(notes = notes, links = links)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LinksUiState())

    fun createLink(noteAId: String, noteBId: String, label: String?) {
        viewModelScope.launch { linkRepository.linkNotes(noteAId, noteBId, label) }
    }

    fun deleteLink(link: NoteLink) {
        viewModelScope.launch { linkRepository.delete(link) }
    }
}
