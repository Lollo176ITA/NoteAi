package com.lorenzocensi.noteai.ui.projects

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lorenzocensi.noteai.R
import com.lorenzocensi.noteai.domain.model.Project
import com.lorenzocensi.noteai.ui.components.CorkboardBackground
import com.lorenzocensi.noteai.ui.theme.PostItPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    onProjectClick: (Project) -> Unit,
    onSettingsClick: () -> Unit,
    vm: ProjectsViewModel = hiltViewModel()
) {
    val projects by vm.projects.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    var pendingDelete by remember { mutableStateOf<Project?>(null) }

    CorkboardBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.projects_title),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    },
                    actions = {
                        IconButton(onClick = onSettingsClick) {
                            Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings_title))
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color(0xFFFAF3E0),
                        actionIconContentColor = Color(0xFFFAF3E0)
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.new_project))
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (projects.isEmpty()) {
                    Text(
                        text = stringResource(R.string.projects_empty),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFFFAF3E0)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(projects, key = { it.id }) { project ->
                            ProjectPostIt(
                                project = project,
                                onClick = { onProjectClick(project) },
                                onDelete = { pendingDelete = project }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        NewProjectDialog(
            onDismiss = { showDialog = false },
            onConfirm = { name ->
                vm.createProject(name)
                showDialog = false
            }
        )
    }

    pendingDelete?.let { p ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(p.name, style = MaterialTheme.typography.headlineSmall) },
            text = { Text(stringResource(R.string.delete) + "?") },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteProject(p)
                    pendingDelete = null
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

@Composable
private fun ProjectPostIt(
    project: Project,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = remember(project.id) { PostItPalette.colorsFor(project.id) }
    val rotation = remember(project.id) { (PostItPalette.rotationDeg(project.id) * 0.5f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .graphicsLayer { rotationZ = rotation }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(8.dp),
                    spotColor = Color.Black.copy(alpha = 0.55f),
                    ambientColor = Color.Black.copy(alpha = 0.30f)
                )
                .background(
                    brush = Brush.linearGradient(listOf(colors.top, colors.bottom)),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = project.name,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF2A1F12),
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            )
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = Color(0xFF6E5B45)
                )
            }
        }

        Pin(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 18.dp, y = (-6).dp)
        )
    }
}

@Composable
private fun Pin(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(20.dp)
            .shadow(elevation = 3.dp, shape = CircleShape, spotColor = Color.Black.copy(alpha = 0.5f))
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFF8B8B), Color(0xFFC92C2C))
                ),
                shape = CircleShape
            )
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .offset(x = 4.dp, y = 4.dp)
                .background(Color.White.copy(alpha = 0.7f), CircleShape)
        )
    }
}

@Composable
private fun NewProjectDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.new_project), style = MaterialTheme.typography.headlineSmall) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.project_name_hint)) },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name) }, enabled = name.isNotBlank()) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}
