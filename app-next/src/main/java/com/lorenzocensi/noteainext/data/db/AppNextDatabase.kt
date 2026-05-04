package com.lorenzocensi.noteainext.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lorenzocensi.noteainext.data.db.dao.NoteDao
import com.lorenzocensi.noteainext.data.db.dao.NoteLinkDao
import com.lorenzocensi.noteainext.data.db.dao.ProjectDao
import com.lorenzocensi.noteainext.data.db.entity.NoteEntity
import com.lorenzocensi.noteainext.data.db.entity.NoteLinkEntity
import com.lorenzocensi.noteainext.data.db.entity.ProjectEntity

@Database(
    entities = [ProjectEntity::class, NoteEntity::class, NoteLinkEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppNextDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun noteDao(): NoteDao
    abstract fun noteLinkDao(): NoteLinkDao
}
