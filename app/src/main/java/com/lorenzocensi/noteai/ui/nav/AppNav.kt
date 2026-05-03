package com.lorenzocensi.noteai.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lorenzocensi.noteai.ui.editor.NoteEditorScreen
import com.lorenzocensi.noteai.ui.notes.NotesScreen
import com.lorenzocensi.noteai.ui.projects.ProjectsScreen
import com.lorenzocensi.noteai.ui.settings.SettingsScreen

object Routes {
    const val PROJECTS = "projects"
    const val PROJECT = "project/{projectId}"
    const val EDITOR = "note/{noteId}"
    const val SETTINGS = "settings"

    fun project(id: String) = "project/$id"
    fun editor(noteId: String) = "note/$noteId"
}

@Composable
fun AppNav() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.PROJECTS) {
        composable(Routes.PROJECTS) {
            ProjectsScreen(
                onProjectClick = { nav.navigate(Routes.project(it.id)) },
                onSettingsClick = { nav.navigate(Routes.SETTINGS) }
            )
        }
        composable(
            route = Routes.PROJECT,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) {
            NotesScreen(
                onBack = { nav.popBackStack() },
                onNoteClick = { nav.navigate(Routes.editor(it.id)) }
            )
        }
        composable(
            route = Routes.EDITOR,
            arguments = listOf(navArgument("noteId") { type = NavType.StringType })
        ) {
            NoteEditorScreen(
                onBack = { nav.popBackStack() },
                onLinkClick = { otherNoteId ->
                    nav.navigate(Routes.editor(otherNoteId)) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { nav.popBackStack() })
        }
    }
}
