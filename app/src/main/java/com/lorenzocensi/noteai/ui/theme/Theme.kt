package com.lorenzocensi.noteai.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF8C5A2B),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFD9B0),
    onPrimaryContainer = Color(0xFF3D2510),
    secondary = Color(0xFF6E5B45),
    secondaryContainer = Color(0xFFF1DCC3),
    onSecondaryContainer = Color(0xFF2A1F12),
    background = Color(0xFFB58A56),
    onBackground = Color(0xFF231910),
    surface = Color(0xFFFAF3E0),
    onSurface = Color(0xFF231910),
    surfaceVariant = Color(0xFFEFE3CB),
    onSurfaceVariant = Color(0xFF4F4031),
    error = Color(0xFFB3261E),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B)
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NoteAiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) darkColorScheme() else LightColors
    MaterialExpressiveTheme(
        colorScheme = colors,
        motionScheme = MotionScheme.expressive(),
        typography = NoteAiTypography,
        content = content
    )
}
