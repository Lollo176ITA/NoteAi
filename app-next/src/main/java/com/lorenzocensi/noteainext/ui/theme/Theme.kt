package com.lorenzocensi.noteainext.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = Color(0xFF5F6F52),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE1EBCF),
    onPrimaryContainer = Color(0xFF1C2417),
    secondary = Color(0xFF6E5F4A),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3DFC2),
    onSecondaryContainer = Color(0xFF261A0D),
    tertiary = Color(0xFF4D6C7C),
    tertiaryContainer = Color(0xFFCFE8F3),
    background = Color(0xFFFCFBF7),
    onBackground = Color(0xFF1F1D1A),
    surface = Color(0xFFFCFBF7),
    onSurface = Color(0xFF1F1D1A),
    surfaceVariant = Color(0xFFE7E2D8),
    onSurfaceVariant = Color(0xFF494640)
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NoteAiNextTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && darkTheme -> dynamicDarkColorScheme(context)
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> dynamicLightColorScheme(context)
        darkTheme -> darkColorScheme()
        else -> LightColors
    }
    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        motionScheme = MotionScheme.expressive(),
        typography = Typography,
        content = content
    )
}
