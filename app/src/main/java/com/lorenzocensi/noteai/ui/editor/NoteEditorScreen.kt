package com.lorenzocensi.noteai.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lorenzocensi.noteai.R
import com.lorenzocensi.noteai.domain.model.SuggestedLink
import com.lorenzocensi.noteai.ui.components.PaperBackground
import com.lorenzocensi.noteai.ui.theme.PostItPalette

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NoteEditorScreen(
    onBack: () -> Unit,
    onLinkClick: (String) -> Unit,
    onOpenSettings: () -> Unit,
    vm: NoteEditorViewModel = hiltViewModel()
) {
    val note by vm.note.collectAsStateWithLifecycle()
    val links by vm.links.collectAsStateWithLifecycle()
    val aiStatus by vm.aiStatus.collectAsStateWithLifecycle()

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

    PaperBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (title.isBlank()) stringResource(R.string.new_note) else title,
                            style = MaterialTheme.typography.headlineSmall,
                            maxLines = 1
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(onClick = { vm.recomputeNow() }) {
                            Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.recompute_links))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                AiStatusBanner(
                    status = aiStatus,
                    onOpenSettings = onOpenSettings,
                    onRetry = { vm.recomputeNow() }
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 64.dp, end = 16.dp, top = 8.dp, bottom = 24.dp)
                ) {
                    BasicTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            vm.update(it, body)
                        },
                        textStyle = MaterialTheme.typography.headlineMedium.copy(
                            color = Color(0xFF231910),
                            fontWeight = FontWeight.Bold
                        ),
                        cursorBrush = SolidColor(Color(0xFF231910)),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { inner ->
                            if (title.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.note_title_hint),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color(0xFF8C7A60)
                                )
                            }
                            inner()
                        }
                    )
                    Spacer(Modifier.height(16.dp))

                    BasicTextField(
                        value = body,
                        onValueChange = {
                            body = it
                            vm.update(title, it)
                        },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF2A1F12),
                            lineHeight = 32.sp
                        ),
                        cursorBrush = SolidColor(Color(0xFF231910)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 320.dp),
                        decorationBox = { inner ->
                            if (body.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.note_body_hint),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color(0xFF8C7A60)
                                )
                            }
                            inner()
                        }
                    )

                    Spacer(Modifier.height(28.dp))
                    HorizontalDivider(color = Color(0xFF8C7A60).copy(alpha = 0.4f))
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.links_section_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(Modifier.height(8.dp))
                    if (links.isEmpty()) {
                        Text(
                            text = stringResource(R.string.links_section_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF6E5B45)
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
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        links.forEach { link ->
            val otherId = link.otherEndOf(currentNoteId)
            var title by remember(otherId) { mutableStateOf<String?>(null) }
            LaunchedEffect(otherId) { title = titleResolver(otherId) }
            val chipColors = remember(otherId) { PostItPalette.colorsFor(otherId) }
            Column {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(chipColors.bottom)
                ) {
                    AssistChip(
                        onClick = { onLinkClick(otherId) },
                        label = {
                            Text(
                                text = title ?: link.reason.take(40),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF2A1F12)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Color.Transparent,
                            labelColor = Color(0xFF2A1F12)
                        ),
                        border = null
                    )
                }
                Text(
                    text = link.reason,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6E5B45),
                    maxLines = 2,
                    modifier = Modifier.padding(start = 12.dp, top = 2.dp)
                )
            }
        }
    }
}
