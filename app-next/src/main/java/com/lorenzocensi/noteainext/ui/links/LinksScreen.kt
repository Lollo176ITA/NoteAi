package com.lorenzocensi.noteainext.ui.links

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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lorenzocensi.noteainext.domain.model.Note
import com.lorenzocensi.noteainext.domain.model.NoteLinkPreview
import com.lorenzocensi.noteainext.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinksScreen(
    vm: LinksViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }
    val canCreateLink = state.notes.size >= 2

    Scaffold(
        topBar = { TopAppBar(title = { Text("Link") }) },
        floatingActionButton = {
            if (canCreateLink) {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier.testTag("create-link")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nuovo link")
                }
            }
        }
    ) { padding ->
        if (state.links.isEmpty()) {
            EmptyState(
                title = "Nessun collegamento",
                body = if (canCreateLink) "Collega manualmente due note correlate." else "Crea almeno due note per aggiungere un link.",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.links, key = { it.link.id }) { preview ->
                    LinkRow(
                        preview = preview,
                        onDelete = { vm.deleteLink(preview.link) }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        NewLinkDialog(
            notes = state.notes,
            onDismiss = { showCreateDialog = false },
            onConfirm = { a, b, label ->
                vm.createLink(a, b, label)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun LinkRow(
    preview: NoteLinkPreview,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("${preview.noteATitle} / ${preview.noteBTitle}", style = MaterialTheme.typography.titleMedium)
                Text(preview.link.label ?: "Collegamento manuale", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.Delete, contentDescription = "Elimina link")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewLinkDialog(
    notes: List<Note>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String?) -> Unit
) {
    var firstId by remember(notes) { mutableStateOf(notes.firstOrNull()?.id.orEmpty()) }
    var secondId by remember(notes) { mutableStateOf(notes.drop(1).firstOrNull()?.id.orEmpty()) }
    var label by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuovo link") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NoteDropdown("Prima nota", notes, firstId) { firstId = it }
                NoteDropdown("Seconda nota", notes, secondId) { secondId = it }
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Etichetta opzionale") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(firstId, secondId, label) },
                enabled = firstId.isNotBlank() && secondId.isNotBlank() && firstId != secondId
            ) {
                Text("Crea")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annulla") }
        }
    )
}

@Composable
private fun NoteDropdown(
    label: String,
    notes: List<Note>,
    selectedId: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedTitle = notes.firstOrNull { it.id == selectedId }?.displayTitle().orEmpty()
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedTitle, modifier = Modifier.weight(1f))
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                notes.forEach { note ->
                    DropdownMenuItem(
                        text = { Text(note.displayTitle()) },
                        onClick = {
                            onSelected(note.id)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

private fun Note.displayTitle(): String = title.ifBlank { body.lineSequence().firstOrNull().orEmpty() }.ifBlank { "Senza titolo" }
