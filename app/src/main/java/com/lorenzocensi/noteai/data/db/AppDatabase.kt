package com.lorenzocensi.noteai.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lorenzocensi.noteai.data.db.dao.ConnectionDao
import com.lorenzocensi.noteai.data.db.dao.NoteDao
import com.lorenzocensi.noteai.data.db.dao.ProjectDao
import com.lorenzocensi.noteai.data.db.entity.ConnectionEntity
import com.lorenzocensi.noteai.data.db.entity.NoteEntity
import com.lorenzocensi.noteai.data.db.entity.ProjectEntity

@Database(
    entities = [ProjectEntity::class, NoteEntity::class, ConnectionEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun noteDao(): NoteDao
    abstract fun connectionDao(): ConnectionDao
}
