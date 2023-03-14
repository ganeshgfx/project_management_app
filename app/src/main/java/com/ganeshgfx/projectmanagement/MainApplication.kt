package com.ganeshgfx.projectmanagement

import android.app.Application
import com.ganeshgfx.projectmanagement.database.ProjectDatabase
import com.ganeshgfx.projectmanagement.di.AppContainer
import com.ganeshgfx.projectmanagement.repositories.ProjectRepository
import com.ganeshgfx.projectmanagement.repositories.TaskListRepository
import com.google.android.material.color.DynamicColors.applyToActivitiesIfAvailable

class MainApplication : Application() {

    private lateinit var projectRepository: ProjectRepository
    private lateinit var taskListRepository: TaskListRepository

    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        //setting dynamic colors to app
        applyToActivitiesIfAvailable(this)

        projectRepository = ProjectRepository(
            ProjectDatabase.getDatabase(applicationContext)
        )

        taskListRepository = TaskListRepository(
            ProjectDatabase.getDatabase(applicationContext)
        )

        appContainer = AppContainer(this)
    }
}