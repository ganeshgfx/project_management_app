package com.ganeshgfx.projectmanagement.di

import androidx.lifecycle.ViewModelProvider
import com.ganeshgfx.projectmanagement.repositories.ProjectRepository
import com.ganeshgfx.projectmanagement.repositories.TaskListRepository
import com.ganeshgfx.projectmanagement.viewModels.ProjectViewModel
import com.ganeshgfx.projectmanagement.viewModels.ProjectViewModelFactory
import com.ganeshgfx.projectmanagement.viewModels.TaskListViewModelFactory

class TaskListContainer(
    repository: TaskListRepository
) {
    val taskListViewModelFactory =  TaskListViewModelFactory(repository)
}