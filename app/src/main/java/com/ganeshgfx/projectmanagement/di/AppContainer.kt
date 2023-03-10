package com.ganeshgfx.projectmanagement.di

import android.content.Context
import com.ganeshgfx.projectmanagement.database.ProjectDatabase
import com.ganeshgfx.projectmanagement.repositories.ProjectRepository

class AppContainer(context: Context) {
    val projectRepository = ProjectRepository(ProjectDatabase.getDatabase(context))

    var projectContainer : ProjectContainer? = null

}