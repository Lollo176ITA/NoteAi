package com.lorenzocensi.noteai.ui.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.absoluteValue

object PostItPalette {

    val PASTELS = listOf(
        Color(0xFFFFF6A5), // giallo classico
        Color(0xFFB5E8D5), // menta
        Color(0xFFFFD4D4), // rosa
        Color(0xFFC8D8FF), // azzurro
        Color(0xFFF4C2FF), // lilla
        Color(0xFFFFE0B5), // pesca
        Color(0xFFD9F2D9), // verde tenue
        Color(0xFFE8DAFF)  // lavanda
    )

    val TAPES = listOf(
        Color(0xFFB6E0FF), // azzurro
        Color(0xFFFFD6A5), // pesca
        Color(0xFFC8F0C8), // menta
        Color(0xFFFFC8DD), // rosa
        Color(0xFFE0CFFB)  // lavanda
    )

    data class PostItColors(val top: Color, val bottom: Color, val edge: Color)

    fun forId(id: String): Color {
        val idx = id.hashCode().absoluteValue % PASTELS.size
        return PASTELS[idx]
    }

    fun colorsFor(id: String): PostItColors {
        val base = forId(id)
        val top = base.lighten(0.10f)
        val edge = base.darken(0.08f)
        return PostItColors(top = top, bottom = base, edge = edge)
    }

    fun tapeFor(id: String): Color {
        val idx = (id.hashCode().absoluteValue / 7) % TAPES.size
        return TAPES[idx]
    }

    fun rotationDeg(id: String): Float {
        val raw = (id.hashCode() % 11) - 5
        return raw.toFloat()
    }
}

private fun Color.lighten(amount: Float): Color = Color(
    red = (red + (1f - red) * amount).coerceIn(0f, 1f),
    green = (green + (1f - green) * amount).coerceIn(0f, 1f),
    blue = (blue + (1f - blue) * amount).coerceIn(0f, 1f),
    alpha = alpha
)

private fun Color.darken(amount: Float): Color = Color(
    red = (red * (1f - amount)).coerceIn(0f, 1f),
    green = (green * (1f - amount)).coerceIn(0f, 1f),
    blue = (blue * (1f - amount)).coerceIn(0f, 1f),
    alpha = alpha
)
