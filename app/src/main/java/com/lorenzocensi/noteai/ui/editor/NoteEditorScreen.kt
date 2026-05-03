package com.lorenzocensi.noteai.ui.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lorenzocensi.noteai.R
import com.lorenzocensi.noteai.domain.model.SuggestedLink

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NoteEditorScreen(
    onBack: () -> Unit,
    onLinkClick: (String) -> Unit,
    vm: NoteEditorViewModel = hiltViewModel()
) {
    val note by vm.note.collectAsStateWithLifecycle()
    val links by vm.links.collectAsStateWithLifecycle()

    var title by remember(note?.id) { mutableStateOf(note?.title.orEmpty()) }
    var body by remember(note?.id) { mutableStateOf(note?.body.orEmpty()) }
    var initialized by remember(note?.id) { mutableStateOf(false) }

    LaunchedEffect(note) {
        val n = note
        if (n != null && !initialized) {
            title = n.title
            body = n.body
            initialized = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (title.isBlank()) stringResource(R.string.new_note) else title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { vm.recomputeNow() }) {
                        Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.recompute_links))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    vm.update(it, body)
                },
                label = { Text(stringResource(R.string.note_title_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = body,
                onValueChange = {
                    body = it
                    vm.update(title, it)
                },
                label = { Text(stringResource(R.string.note_body_hint)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 240.dp)
            )

            Spacer(Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.links_section_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.height(8.dp))
            if (links.isEmpty()) {
                Text(
                    text = stringResource(R.string.links_section_empty),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LinksSection(
                    links = links,
                    currentNoteId = note?.id,
                    onLinkClick = onLinkClick,
                    titleResolver = { id -> vm.lookupLinkedNoteTitle(id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LinksSection(
    links: List<SuggestedLink>,
    currentNoteId: String?,
    onLinkClick: (String) -> Unit,
    titleResolver: suspend (String) -> String?
) {
    if (currentNoteId == null) return
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        links.forEach { link ->
            val otherId = link.otherEndOf(currentNoteId)
            var title by remember(otherId) { mutableStateOf<String?>(null) }
            LaunchedEffect(otherId) { title = titleResolver(otherId) }
            Column {
                AssistChip(
                    onClick = { onLinkClick(otherId) },
                    label = { Text(title ?: link.reason.take(40)) }
                )
                Text(
                    text = link.reason,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                )
            }
        }
    }
}
