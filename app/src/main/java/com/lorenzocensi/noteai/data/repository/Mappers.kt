package com.lorenzocensi.noteai.data.repository

import com.lorenzocensi.noteai.data.db.entity.ConnectionEntity
import com.lorenzocensi.noteai.data.db.entity.NoteEntity
import com.lorenzocensi.noteai.data.db.entity.ProjectEntity
import com.lorenzocensi.noteai.domain.model.Note
import com.lorenzocensi.noteai.domain.model.Project
import com.lorenzocensi.noteai.domain.model.SuggestedLink

fun ProjectEntity.toDomain() = Project(id, name, colorSeed, createdAt)
fun Project.toEntity() = ProjectEntity(id, name, colorSeed, createdAt)

fun NoteEntity.toDomain() = Note(id, projectId, title, body, createdAt, updatedAt)
fun Note.toEntity() = NoteEntity(id, projectId, title, body, createdAt, updatedAt)

fun ConnectionEntity.toDomain() = SuggestedLink(id, noteAId, noteBId, reason, score)
