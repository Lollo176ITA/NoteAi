package com.lorenzocensi.noteai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.lorenzocensi.noteai.ui.nav.AppNav
import com.lorenzocensi.noteai.ui.theme.NoteAiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteAiTheme {
                AppNav()
            }
        }
    }
}
