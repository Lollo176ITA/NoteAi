package com.lorenzocensi.noteainext.data.repository

import com.lorenzocensi.noteainext.data.db.entity.NoteEntity
import com.lorenzocensi.noteainext.data.db.entity.NoteLinkEntity
import com.lorenzocensi.noteainext.data.db.entity.ProjectEntity
import com.lorenzocensi.noteainext.domain.model.Note
import com.lorenzocensi.noteainext.domain.model.NoteLink
import com.lorenzocensi.noteainext.domain.model.Project

fun ProjectEntity.toDomain() = Project(id, name, colorSeed, createdAt, updatedAt)
fun Project.toEntity() = ProjectEntity(id, name, colorSeed, createdAt, updatedAt)

fun NoteEntity.toDomain() = Note(id, projectId, title, body, createdAt, updatedAt)
fun Note.toEntity() = NoteEntity(id, projectId, title, body, createdAt, updatedAt)

fun NoteLinkEntity.toDomain() = NoteLink(id, noteAId, noteBId, label, createdAt)
fun NoteLink.toEntity() = NoteLinkEntity(id, noteAId, noteBId, label, createdAt)
