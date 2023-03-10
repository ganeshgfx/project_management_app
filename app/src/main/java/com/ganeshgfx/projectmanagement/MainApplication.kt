package com.ganeshgfx.projectmanagement

import android.app.Application
import com.ganeshgfx.projectmanagement.database.ProjectDatabase
import com.ganeshgfx.projectmanagement.di.AppContainer
import com.ganeshgfx.projectmanagement.repositories.ProjectRepository
import com.google.android.material.color.DynamicColors.applyToActivitiesIfAvailable

class MainApplication : Application() {

    private lateinit var projectRepository: ProjectRepository

    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        //setting dynamic colors to app
        applyToActivitiesIfAvailable(this)

        projectRepository = ProjectRepository(
            ProjectDatabase.getDatabase(applicationContext)
        )

        appContainer = AppContainer(this)
    }
}