package com.lorenzocensi.noteainext.ui.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lorenzocensi.noteainext.data.repository.NoteRepository
import com.lorenzocensi.noteainext.data.repository.ProjectRepository
import com.lorenzocensi.noteainext.domain.model.Note
import com.lorenzocensi.noteainext.domain.model.Project
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {
    val projects: StateFlow<List<Project>> = projectRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun createProject(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch { projectRepository.create(name) }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch { projectRepository.delete(project) }
    }

    fun createNote(project: Project, onCreated: (Note) -> Unit) {
        viewModelScope.launch { onCreated(noteRepository.create(projectId = project.id)) }
    }
}
