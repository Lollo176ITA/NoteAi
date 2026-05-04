package com.lorenzocensi.noteai.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

private val CorkBase = Color(0xFFB58A56)
private val CorkDarkSpot = Color(0xFF6F4A24)
private val CorkLightSpot = Color(0xFFD9B07A)

@Composable
fun CorkboardBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(CorkBase, Color(0xFFA37A48))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val seed = 1234L
            val rng = Random(seed)
            val w = size.width
            val h = size.height
            val density = ((w * h) / 1800f).toInt().coerceAtMost(2400)
            repeat(density) {
                val x = rng.nextFloat() * w
                val y = rng.nextFloat() * h
                val r = rng.nextFloat() * 1.6f + 0.6f
                val dark = rng.nextFloat() > 0.45f
                drawOval(
                    color = if (dark) CorkDarkSpot.copy(alpha = 0.20f) else CorkLightSpot.copy(alpha = 0.18f),
                    topLeft = Offset(x, y),
                    size = Size(r * 2.4f, r * 1.6f)
                )
            }
        }
        content()
    }
}
