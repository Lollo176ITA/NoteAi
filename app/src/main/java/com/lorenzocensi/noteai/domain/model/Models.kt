package com.lorenzocensi.noteai.domain.model

data class Project(
    val id: String,
    val name: String,
    val colorSeed: Int,
    val createdAt: Long
)

data class Note(
    val id: String,
    val projectId: String,
    val title: String,
    val body: String,
    val createdAt: Long,
    val updatedAt: Long
)

data class SuggestedLink(
    val id: Long,
    val noteAId: String,
    val noteBId: String,
    val reason: String,
    val score: Float?
) {
    fun otherEndOf(noteId: String): String =
        if (noteAId == noteId) noteBId else noteAId
}
