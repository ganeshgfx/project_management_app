package com.ganeshgfx.projectmanagement.di

import androidx.lifecycle.ViewModelProvider
import com.ganeshgfx.projectmanagement.repositories.ProjectRepository
import com.ganeshgfx.projectmanagement.viewModels.ProjectViewModel
import com.ganeshgfx.projectmanagement.viewModels.ProjectViewModelFactory

class ProjectContainer(
    projectRepository: ProjectRepository
) {
    val projectViewModelFactory =  ProjectViewModelFactory(projectRepository)
}