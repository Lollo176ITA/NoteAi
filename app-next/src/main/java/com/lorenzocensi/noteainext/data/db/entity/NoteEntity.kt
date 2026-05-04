package com.lorenzocensi.noteainext.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("projectId"), Index("updatedAt")]
)
data class NoteEntity(
    @PrimaryKey val id: String,
    val projectId: String?,
    val title: String,
    val body: String,
    val createdAt: Long,
    val updatedAt: Long
)
