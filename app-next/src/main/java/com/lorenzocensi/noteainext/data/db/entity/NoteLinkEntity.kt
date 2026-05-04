package com.lorenzocensi.noteainext.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "note_links",
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["noteAId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["noteBId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("noteAId"), Index("noteBId")]
)
data class NoteLinkEntity(
    @PrimaryKey val id: String,
    val noteAId: String,
    val noteBId: String,
    val label: String?,
    val createdAt: Long
)
