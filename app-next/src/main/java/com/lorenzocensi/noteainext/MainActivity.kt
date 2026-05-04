package com.lorenzocensi.noteainext

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.lorenzocensi.noteainext.ui.nav.AppRoot
import com.lorenzocensi.noteainext.ui.theme.NoteAiNextTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteAiNextTheme {
                AppRoot()
            }
        }
    }
}
