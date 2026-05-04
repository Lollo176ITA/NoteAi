package com.lorenzocensi.noteainext.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lorenzocensi.noteainext.domain.model.Note
import com.lorenzocensi.noteainext.ui.theme.NotePalette

@Composable
fun NoteCard(
    note: Note,
    projectName: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = remember(note.id) { NotePalette.forId(note.id) }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        color = colors.container,
        tonalElevation = 1.dp,
        shadowElevation = 1.dp,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(
                modifier = Modifier
                    .height(3.dp)
                    .fillMaxWidth(0.28f)
                    .clip(RoundedCornerShape(50))
                    .background(colors.accent)
            )
            Text(
                text = note.title.ifBlank { "Senza titolo" },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color(0xFF22201B)
            )
            if (note.body.isNotBlank()) {
                Text(
                    text = note.body,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(0xFF494235)
                )
            }
            if (!projectName.isNullOrBlank()) {
                Text(
                    text = projectName,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF5F5748)
                )
            }
        }
    }
}
