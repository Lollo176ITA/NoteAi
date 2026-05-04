package com.lorenzocensi.noteainext.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lorenzocensi.noteainext.data.repository.NoteRepository
import com.lorenzocensi.noteainext.data.repository.ProjectRepository
import com.lorenzocensi.noteainext.domain.model.Note
import com.lorenzocensi.noteainext.domain.model.Project
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NoteListItem(
    val note: Note,
    val projectName: String?
)

data class NotesUiState(
    val query: String = "",
    val selectedProjectId: String? = null,
    val projects: List<Project> = emptyList(),
    val notes: List<NoteListItem> = emptyList()
)

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    projectRepository: ProjectRepository
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val selectedProjectId = MutableStateFlow<String?>(null)
    private val projects = projectRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val notes = combine(query, selectedProjectId) { q, projectId -> q to projectId }
        .flatMapLatest { (q, projectId) -> noteRepository.observeFiltered(q, projectId) }

    val uiState = combine(query, selectedProjectId, projects, notes) { q, projectId, allProjects, allNotes ->
        val projectNames = allProjects.associate { it.id to it.name }
        NotesUiState(
            query = q,
            selectedProjectId = projectId,
            projects = allProjects,
            notes = allNotes.map { note -> NoteListItem(note, note.projectId?.let(projectNames::get)) }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NotesUiState())

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun onProjectFilterChange(projectId: String?) {
        selectedProjectId.value = projectId
    }

    fun createNote(onCreated: (Note) -> Unit) {
        viewModelScope.launch {
            onCreated(noteRepository.create(projectId = selectedProjectId.value))
        }
    }
}
