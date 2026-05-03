package com.lorenzocensi.noteai.data.repository

import com.lorenzocensi.noteai.data.db.dao.ProjectDao
import com.lorenzocensi.noteai.domain.model.Project
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepository @Inject constructor(
    private val dao: ProjectDao
) {
    fun observeAll(): Flow<List<Project>> = dao.observeAll().map { list -> list.map { it.toDomain() } }
    fun observeNoteCount(projectId: String): Flow<Int> = dao.observeNoteCount(projectId)
    suspend fun upsert(project: Project) = dao.upsert(project.toEntity())
    suspend fun delete(project: Project) = dao.delete(project.toEntity())
    suspend fun getById(id: String): Project? = dao.getById(id)?.toDomain()
}
