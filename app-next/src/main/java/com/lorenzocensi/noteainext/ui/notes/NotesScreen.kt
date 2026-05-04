package com.lorenzocensi.noteainext.ui.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lorenzocensi.noteainext.domain.model.Note
import com.lorenzocensi.noteainext.ui.components.EmptyState
import com.lorenzocensi.noteainext.ui.components.NoteCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    onOpenNote: (Note) -> Unit,
    vm: NotesViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    var filtersExpanded by remember { mutableStateOf(false) }
    val selectedProjectName = state.projects.firstOrNull { it.id == state.selectedProjectId }?.name

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Note") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { vm.createNote(onOpenNote) },
                modifier = Modifier.testTag("create-note")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuova nota")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = vm::onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("notes-search"),
                singleLine = true,
                label = { Text("Cerca in titolo e corpo") }
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box {
                    AssistChip(
                        onClick = { filtersExpanded = true },
                        leadingIcon = { Icon(Icons.Default.FilterList, contentDescription = null) },
                        label = { Text(selectedProjectName ?: "Tutti i progetti") }
                    )
                    DropdownMenu(
                        expanded = filtersExpanded,
                        onDismissRequest = { filtersExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Tutti i progetti") },
                            onClick = {
                                vm.onProjectFilterChange(null)
                                filtersExpanded = false
                            }
                        )
                        state.projects.forEach { project ->
                            DropdownMenuItem(
                                text = { Text(project.name) },
                                onClick = {
                                    vm.onProjectFilterChange(project.id)
                                    filtersExpanded = false
                                }
                            )
                        }
                    }
                }
                Text(
                    text = "${state.notes.size} note",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (state.notes.isEmpty()) {
                EmptyState(
                    title = "Nessuna nota",
                    body = "Crea una nota o modifica i filtri di ricerca.",
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("notes-list"),
                    contentPadding = PaddingValues(bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.notes, key = { it.note.id }) { item ->
                        NoteCard(
                            note = item.note,
                            projectName = item.projectName,
                            onClick = { onOpenNote(item.note) }
                        )
                    }
                }
            }
        }
    }
}
