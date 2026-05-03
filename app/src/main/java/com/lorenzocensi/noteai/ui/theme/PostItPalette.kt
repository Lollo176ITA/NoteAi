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

    fun forId(id: String): Color {
        val idx = id.hashCode().absoluteValue % PASTELS.size
        return PASTELS[idx]
    }

    fun rotationDeg(id: String): Float {
        val raw = (id.hashCode() % 7) - 3
        return raw.toFloat()
    }
}
