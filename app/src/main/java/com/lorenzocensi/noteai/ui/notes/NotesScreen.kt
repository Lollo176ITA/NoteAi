package com.lorenzocensi.noteai.ui.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lorenzocensi.noteai.R
import com.lorenzocensi.noteai.domain.model.Note
import com.lorenzocensi.noteai.ui.components.CorkboardBackground
import com.lorenzocensi.noteai.ui.components.PostItCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    onBack: () -> Unit,
    onNoteClick: (Note) -> Unit,
    vm: NotesViewModel = hiltViewModel()
) {
    val notes by vm.notes.collectAsStateWithLifecycle()
    val project by vm.project.collectAsStateWithLifecycle()

    CorkboardBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            project?.name ?: "",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cancel))
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color(0xFFFAF3E0),
                        navigationIconContentColor = Color(0xFFFAF3E0)
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { vm.createEmptyNote(onCreated = onNoteClick) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.new_note))
                }
            }
        ) { padding ->
            if (notes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    val placeholder = remember {
                        Note(
                            id = "empty-placeholder",
                            projectId = "",
                            title = "Inizia qui!",
                            body = "Tocca + per il primo post-it.",
                            createdAt = 0L,
                            updatedAt = 0L
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 48.dp)
                            .widthIn(max = 240.dp)
                    ) {
                        PostItCard(note = placeholder, onClick = {})
                    }
                }
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(minSize = 168.dp),
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalItemSpacing = 12.dp,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notes, key = { it.id }) { note ->
                        PostItCard(note = note, onClick = { onNoteClick(note) })
                    }
                }
            }
        }
    }
}
