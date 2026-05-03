package com.lorenzocensi.noteai.data.repository

import com.lorenzocensi.noteai.data.db.dao.ConnectionDao
import com.lorenzocensi.noteai.data.db.entity.ConnectionEntity
import com.lorenzocensi.noteai.domain.model.SuggestedLink
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectionRepository @Inject constructor(
    private val dao: ConnectionDao
) {
    fun observeForNote(noteId: String): Flow<List<SuggestedLink>> =
        dao.observeForNote(noteId).map { list -> list.map { it.toDomain() } }

    suspend fun replaceForNote(noteId: String, items: List<ConnectionEntity>) {
        dao.replaceForNote(noteId, items)
    }
}
