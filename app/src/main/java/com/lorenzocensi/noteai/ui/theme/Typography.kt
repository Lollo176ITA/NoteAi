package com.lorenzocensi.noteai.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.lorenzocensi.noteai.R

val Caveat = FontFamily(
    Font(R.font.caveat_regular, FontWeight.Normal),
    Font(R.font.caveat_bold, FontWeight.Bold)
)

private val defaults = Typography()

val NoteAiTypography = Typography(
    displayLarge = defaults.displayLarge.copy(fontFamily = Caveat, fontWeight = FontWeight.Bold),
    displayMedium = defaults.displayMedium.copy(fontFamily = Caveat, fontWeight = FontWeight.Bold),
    displaySmall = defaults.displaySmall.copy(fontFamily = Caveat, fontWeight = FontWeight.Bold),
    headlineLarge = defaults.headlineLarge.copy(fontFamily = Caveat, fontWeight = FontWeight.Bold),
    headlineMedium = defaults.headlineMedium.copy(fontFamily = Caveat, fontWeight = FontWeight.Bold),
    headlineSmall = defaults.headlineSmall.copy(fontFamily = Caveat, fontWeight = FontWeight.Bold),
    titleLarge = defaults.titleLarge.copy(fontFamily = Caveat, fontWeight = FontWeight.Bold),
    bodyLarge = TextStyle(
        fontFamily = Caveat,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 32.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Caveat,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Caveat,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 20.sp
    )
)
