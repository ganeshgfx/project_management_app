package com.ganeshgfx.projectmanagement.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ganeshgfx.projectmanagement.repositories.ProjectRepository

class ProjectViewModelFactory(private val projectRepository: ProjectRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProjectViewModel::class.java)) {
            return ProjectViewModel(projectRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}