package com.lorenzocensi.noteainext.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.lorenzocensi.noteainext.data.db.entity.NoteLinkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteLinkDao {
    @Query("SELECT * FROM note_links ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<NoteLinkEntity>>

    @Query("SELECT * FROM note_links WHERE noteAId = :noteId OR noteBId = :noteId ORDER BY createdAt DESC")
    fun observeForNote(noteId: String): Flow<List<NoteLinkEntity>>

    @Upsert
    suspend fun upsert(link: NoteLinkEntity)

    @Delete
    suspend fun delete(link: NoteLinkEntity)
}
