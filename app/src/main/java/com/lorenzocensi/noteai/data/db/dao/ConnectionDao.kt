package com.lorenzocensi.noteai.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.lorenzocensi.noteai.data.db.entity.ConnectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConnectionDao {

    @Query("SELECT * FROM connections WHERE noteAId = :noteId OR noteBId = :noteId ORDER BY score DESC, createdAt DESC")
    fun observeForNote(noteId: String): Flow<List<ConnectionEntity>>

    @Query("DELETE FROM connections WHERE noteAId = :noteId OR noteBId = :noteId")
    suspend fun deleteAllForNote(noteId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ConnectionEntity>)

    @Transaction
    suspend fun replaceForNote(noteId: String, items: List<ConnectionEntity>) {
        deleteAllForNote(noteId)
        if (items.isNotEmpty()) insertAll(items)
    }
}
