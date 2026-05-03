package com.lorenzocensi.noteai.domain.usecase

import com.lorenzocensi.noteai.data.repository.ProjectRepository
import com.lorenzocensi.noteai.domain.model.Project
import javax.inject.Inject

class DeleteProjectUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(project: Project) = projectRepository.delete(project)
}
