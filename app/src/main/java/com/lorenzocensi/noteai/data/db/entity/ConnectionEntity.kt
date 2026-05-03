package com.lorenzocensi.noteai.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "connections",
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
    indices = [
        Index(value = ["noteAId", "noteBId"], unique = true),
        Index("noteAId"),
        Index("noteBId")
    ]
)
data class ConnectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val noteAId: String,
    val noteBId: String,
    val reason: String,
    val score: Float?,
    val createdAt: Long
) {
    companion object {
        fun create(noteX: String, noteY: String, reason: String, score: Float?, now: Long): ConnectionEntity {
            val (a, b) = if (noteX < noteY) noteX to noteY else noteY to noteX
            return ConnectionEntity(
                noteAId = a,
                noteBId = b,
                reason = reason,
                score = score,
                createdAt = now
            )
        }
    }
}
