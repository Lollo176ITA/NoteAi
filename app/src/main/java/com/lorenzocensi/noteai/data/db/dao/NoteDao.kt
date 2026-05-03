package com.lorenzocensi.noteai.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.lorenzocensi.noteai.data.db.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

data class NoteSnippet(
    val id: String,
    val title: String,
    val body: String
)

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes WHERE projectId = :projectId ORDER BY updatedAt DESC")
    fun observeByProject(projectId: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getById(id: String): NoteEntity?

    @Query("SELECT * FROM notes WHERE id = :id")
    fun observeById(id: String): Flow<NoteEntity?>

    @Query("SELECT id, title, substr(body, 1, 200) AS body FROM notes WHERE projectId = :projectId AND id != :excludeId")
    suspend fun snippetsForProject(projectId: String, excludeId: String): List<NoteSnippet>

    @Upsert
    suspend fun upsert(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)
}
