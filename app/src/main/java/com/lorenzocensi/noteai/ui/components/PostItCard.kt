package com.lorenzocensi.noteai.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
    val colors = remember(note.id) { PostItPalette.colorsFor(note.id) }
    val tape = remember(note.id) { PostItPalette.tapeFor(note.id) }
    val rotation = remember(note.id) { PostItPalette.rotationDeg(note.id) }
    val tapeRotation = remember(note.id) { ((note.id.hashCode() % 7) - 3).toFloat() }

    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "postit-press"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 14.dp)
            .graphicsLayer {
                rotationZ = rotation
                scaleX = scale
                scaleY = scale
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(6.dp),
                    spotColor = Color.Black.copy(alpha = 0.55f),
                    ambientColor = Color.Black.copy(alpha = 0.30f)
                )
                .background(
                    brush = Brush.linearGradient(listOf(colors.top, colors.bottom)),
                    shape = RoundedCornerShape(6.dp)
                )
                .clickable(
                    interactionSource = interaction,
                    indication = null,
                    onClick = onClick
                )
                .heightIn(min = 130.dp)
                .padding(horizontal = 14.dp, vertical = 16.dp)
        ) {
            if (note.title.isNotBlank()) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF2A1F12),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
            }
            if (note.body.isNotBlank()) {
                Text(
                    text = note.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF3D2E1A),
                    maxLines = 7,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(width = 64.dp, height = 18.dp)
                .graphicsLayer { rotationZ = tapeRotation }
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(2.dp),
                    spotColor = Color.Black.copy(alpha = 0.3f)
                )
                .background(
                    brush = Brush.verticalGradient(
                        listOf(tape.copy(alpha = 0.95f), tape.copy(alpha = 0.75f))
                    ),
                    shape = RoundedCornerShape(2.dp)
                )
        )
    }
}
