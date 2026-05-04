package com.lorenzocensi.noteainext.ui.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.abs

data class NoteColors(val container: Color, val accent: Color)

object NotePalette {
    private val colors = listOf(
        NoteColors(Color(0xFFFFF4C7), Color(0xFFD6A514)),
        NoteColors(Color(0xFFE2F2D5), Color(0xFF6F9F53)),
        NoteColors(Color(0xFFDDEFF7), Color(0xFF4E8DA8)),
        NoteColors(Color(0xFFF7E0D8), Color(0xFFC07158)),
        NoteColors(Color(0xFFECE4F6), Color(0xFF8C75AF))
    )

    fun forId(id: String): NoteColors = colors[abs(id.hashCode()) % colors.size]
}
