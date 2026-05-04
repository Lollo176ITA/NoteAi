package com.lorenzocensi.noteai.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

private val PaperCream = Color(0xFFFAF3E0)
private val LineBlue = Color(0xFFBED6E8)
private val MarginRed = Color(0xFFE7A8A8)

@Composable
fun PaperBackground(
    modifier: Modifier = Modifier,
    showRuledLines: Boolean = true,
    showMargin: Boolean = true,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val lineSpacingPx = with(density) { 32.dp.toPx() }
    val marginXPx = with(density) { 56.dp.toPx() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PaperCream)
    ) {
        if (showRuledLines || showMargin) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (showRuledLines) {
                    val w = size.width
                    val h = size.height
                    var y = lineSpacingPx
                    while (y < h) {
                        drawLine(
                            color = LineBlue.copy(alpha = 0.55f),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1f
                        )
                        y += lineSpacingPx
                    }
                }
                if (showMargin) {
                    drawLine(
                        color = MarginRed.copy(alpha = 0.7f),
                        start = Offset(marginXPx, 0f),
                        end = Offset(marginXPx, size.height),
                        strokeWidth = 2f
                    )
                }
            }
        }
        content()
    }
}
