package com.ganeshgfx.projectmanagement.di

import android.content.Context
import com.ganeshgfx.projectmanagement.database.ProjectDatabase
import com.ganeshgfx.projectmanagement.repositories.ProjectRepository
import com.ganeshgfx.projectmanagement.repositories.TaskListRepository

class AppContainer(context: Context) {
    val projectRepository = ProjectRepository(ProjectDatabase.getDatabase(context).projectDao())
    val taskListRepository = TaskListRepository(ProjectDatabase.getDatabase(context).projectDao())

    var projectContainer: ProjectContainer? = null
    var taskListContainer: TaskListContainer? = null

}