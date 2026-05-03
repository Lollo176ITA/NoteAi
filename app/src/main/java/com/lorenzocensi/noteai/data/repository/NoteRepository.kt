package com.lorenzocensi.noteai.data.repository

import com.lorenzocensi.noteai.data.db.dao.NoteDao
import com.lorenzocensi.noteai.data.db.dao.NoteSnippet
import com.lorenzocensi.noteai.domain.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val dao: NoteDao
) {
    fun observeByProject(projectId: String): Flow<List<Note>> =
        dao.observeByProject(projectId).map { list -> list.map { it.toDomain() } }

    fun observeById(id: String): Flow<Note?> =
        dao.observeById(id).map { it?.toDomain() }

    suspend fun getById(id: String): Note? = dao.getById(id)?.toDomain()
    suspend fun upsert(note: Note) = dao.upsert(note.toEntity())
    suspend fun delete(note: Note) = dao.delete(note.toEntity())
    suspend fun snippetsForProject(projectId: String, excludeId: String): List<NoteSnippet> =
        dao.snippetsForProject(projectId, excludeId)
}
