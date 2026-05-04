package com.lorenzocensi.noteainext.data.repository

import com.lorenzocensi.noteainext.data.db.dao.NoteDao
import com.lorenzocensi.noteainext.domain.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val dao: NoteDao
) {
    fun observeFiltered(query: String, projectId: String?): Flow<List<Note>> =
        dao.observeFiltered(query.trim(), projectId).map { items -> items.map { it.toDomain() } }

    fun observeAll(): Flow<List<Note>> = dao.observeAll().map { items -> items.map { it.toDomain() } }

    fun observeById(id: String): Flow<Note?> = dao.observeById(id).map { it?.toDomain() }

    suspend fun getById(id: String): Note? = dao.getById(id)?.toDomain()

    suspend fun create(projectId: String? = null): Note {
        val now = System.currentTimeMillis()
        val note = Note(
            id = UUID.randomUUID().toString(),
            projectId = projectId,
            title = "",
            body = "",
            createdAt = now,
            updatedAt = now
        )
        dao.upsert(note.toEntity())
        return note
    }

    suspend fun upsert(note: Note) = dao.upsert(note.toEntity())

    suspend fun delete(note: Note) = dao.delete(note.toEntity())
}
