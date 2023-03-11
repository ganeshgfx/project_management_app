package com.ganeshgfx.projectmanagement.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ganeshgfx.projectmanagement.repositories.ProjectRepository
import com.ganeshgfx.projectmanagement.repositories.TaskListRepository

class TaskListViewModelFactory(private val taskListRepository: TaskListRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskListViewModel::class.java)) {
            return TaskListViewModel(taskListRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}