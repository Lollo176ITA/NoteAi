package com.lorenzocensi.noteai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lorenzocensi.noteai.domain.model.Note
import com.lorenzocensi.noteai.ui.theme.PostItPalette

@Composable
fun PostItCard(
    note: Note,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = remember(note.id) { PostItPalette.forId(note.id) }
    val rotation = remember(note.id) { PostItPalette.rotationDeg(note.id) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(6.dp)
            .graphicsLayer { rotationZ = rotation }
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(2.dp))
            .background(color, RoundedCornerShape(2.dp))
            .clickable { onClick() }
            .heightIn(min = 110.dp)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        if (note.title.isNotBlank()) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = androidx.compose.ui.graphics.Color(0xFF222222),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (note.body.isNotBlank()) {
            Text(
                text = note.body,
                style = MaterialTheme.typography.bodySmall,
                color = androidx.compose.ui.graphics.Color(0xFF333333),
                maxLines = 6,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
