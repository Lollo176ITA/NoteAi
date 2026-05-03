package com.lorenzocensi.noteai.ui.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lorenzocensi.noteai.data.repository.ProjectRepository
import com.lorenzocensi.noteai.domain.model.Project
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val repo: ProjectRepository
) : ViewModel() {

    val projects: StateFlow<List<Project>> = repo.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun createProject(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            repo.upsert(
                Project(
                    id = UUID.randomUUID().toString(),
                    name = name.trim(),
                    colorSeed = name.hashCode(),
                    createdAt = now
                )
            )
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch { repo.delete(project) }
    }
}
