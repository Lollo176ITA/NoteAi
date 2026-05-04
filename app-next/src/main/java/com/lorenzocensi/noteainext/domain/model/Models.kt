package com.lorenzocensi.noteainext.domain.model

data class Project(
    val id: String,
    val name: String,
    val colorSeed: Int,
    val createdAt: Long,
    val updatedAt: Long
)

data class Note(
    val id: String,
    val projectId: String?,
    val title: String,
    val body: String,
    val createdAt: Long,
    val updatedAt: Long
)

data class NoteLink(
    val id: String,
    val noteAId: String,
    val noteBId: String,
    val label: String?,
    val createdAt: Long
) {
    fun otherEndOf(noteId: String): String? = when (noteId) {
        noteAId -> noteBId
        noteBId -> noteAId
        else -> null
    }
}

data class NoteLinkPreview(
    val link: NoteLink,
    val noteATitle: String,
    val noteBTitle: String
)
