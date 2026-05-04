package com.lorenzocensi.noteainext.domain.usecase

import com.lorenzocensi.noteainext.domain.model.Note

object NoteFilters {
    fun matches(note: Note, query: String, projectId: String?): Boolean {
        val projectMatches = projectId == null || note.projectId == projectId
        val normalizedQuery = query.trim()
        val queryMatches = normalizedQuery.isBlank() ||
            note.title.contains(normalizedQuery, ignoreCase = true) ||
            note.body.contains(normalizedQuery, ignoreCase = true)
        return projectMatches && queryMatches
    }
}
