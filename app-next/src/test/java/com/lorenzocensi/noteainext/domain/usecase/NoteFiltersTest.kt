package com.lorenzocensi.noteainext.domain.usecase

import com.lorenzocensi.noteainext.domain.model.Note
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NoteFiltersTest {
    private val note = Note(
        id = "note-1",
        projectId = "project-1",
        title = "Architettura nuova",
        body = "Ricerca locale e link manuali",
        createdAt = 1L,
        updatedAt = 1L
    )

    @Test
    fun queryMatchesTitleOrBodyIgnoringCase() {
        assertTrue(NoteFilters.matches(note, "architettura", null))
        assertTrue(NoteFilters.matches(note, "LINK", null))
        assertFalse(NoteFilters.matches(note, "ai", null))
    }

    @Test
    fun projectFilterRestrictsResults() {
        assertTrue(NoteFilters.matches(note, "", "project-1"))
        assertFalse(NoteFilters.matches(note, "", "project-2"))
    }
}
