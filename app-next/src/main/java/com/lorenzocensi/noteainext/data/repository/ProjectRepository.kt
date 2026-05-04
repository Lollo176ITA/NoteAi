package com.lorenzocensi.noteainext.data.repository

import com.lorenzocensi.noteainext.data.db.dao.ProjectDao
import com.lorenzocensi.noteainext.domain.model.Project
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepository @Inject constructor(
    private val dao: ProjectDao
) {
    fun observeAll(): Flow<List<Project>> = dao.observeAll().map { items -> items.map { it.toDomain() } }

    suspend fun getById(id: String): Project? = dao.getById(id)?.toDomain()

    suspend fun create(name: String): Project {
        val now = System.currentTimeMillis()
        val project = Project(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            colorSeed = name.hashCode(),
            createdAt = now,
            updatedAt = now
        )
        dao.upsert(project.toEntity())
        return project
    }

    suspend fun update(project: Project) = dao.upsert(project.copy(updatedAt = System.currentTimeMillis()).toEntity())

    suspend fun delete(project: Project) = dao.delete(project.toEntity())
}
