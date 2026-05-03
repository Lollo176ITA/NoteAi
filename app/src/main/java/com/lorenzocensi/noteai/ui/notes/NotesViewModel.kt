package com.lorenzocensi.noteai.ui.notes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lorenzocensi.noteai.data.repository.NoteRepository
import com.lorenzocensi.noteai.data.repository.ProjectRepository
import com.lorenzocensi.noteai.domain.model.Note
import com.lorenzocensi.noteai.domain.model.Project
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val noteRepo: NoteRepository,
    projectRepo: ProjectRepository
) : ViewModel() {

    private val projectId: String = checkNotNull(savedStateHandle["projectId"])

    val project: StateFlow<Project?> = MutableStateFlow<Project?>(null).also { flow ->
        viewModelScope.launch { flow.value = projectRepo.getById(projectId) }
    }

    val notes: StateFlow<List<Note>> = noteRepo.observeByProject(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun createEmptyNote(onCreated: (String) -> Unit) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val n = Note(
                id = UUID.randomUUID().toString(),
                projectId = projectId,
                title = "",
                body = "",
                createdAt = now,
                updatedAt = now
            )
            noteRepo.upsert(n)
            onCreated(n.id)
        }
    }
}
