package com.lorenzocensi.noteainext.ui.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lorenzocensi.noteainext.ui.editor.NoteEditorScreen
import com.lorenzocensi.noteainext.ui.links.LinksScreen
import com.lorenzocensi.noteainext.ui.notes.NotesScreen
import com.lorenzocensi.noteainext.ui.projects.ProjectsScreen

private object Routes {
    const val NOTES = "notes"
    const val PROJECTS = "projects"
    const val LINKS = "links"
    const val EDITOR = "editor/{noteId}"

    fun editor(noteId: String) = "editor/$noteId"
}

private data class TabDestination(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit
)

private val tabs = listOf(
    TabDestination(Routes.NOTES, "Note", { Icon(Icons.Default.NoteAlt, contentDescription = null) }),
    TabDestination(Routes.PROJECTS, "Progetti", { Icon(Icons.Default.Folder, contentDescription = null) }),
    TabDestination(Routes.LINKS, "Link", { Icon(Icons.Default.Link, contentDescription = null) })
)

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val showBottomBar = tabs.any { tab ->
        currentDestination?.hierarchy?.any { it.route == tab.route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    tabs.forEach { tab ->
                        val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = tab.icon,
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.NOTES,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.NOTES) {
                NotesScreen(
                    onOpenNote = { navController.navigate(Routes.editor(it.id)) }
                )
            }
            composable(Routes.PROJECTS) {
                ProjectsScreen(
                    onOpenNote = { navController.navigate(Routes.editor(it.id)) }
                )
            }
            composable(Routes.LINKS) {
                LinksScreen()
            }
            composable(
                route = Routes.EDITOR,
                arguments = listOf(navArgument("noteId") { type = NavType.StringType })
            ) {
                NoteEditorScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
