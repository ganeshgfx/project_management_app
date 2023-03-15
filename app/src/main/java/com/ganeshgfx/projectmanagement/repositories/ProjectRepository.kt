package com.ganeshgfx.projectmanagement.repositories

import androidx.lifecycle.MutableLiveData
import com.ganeshgfx.projectmanagement.database.ProjectDAO
import com.ganeshgfx.projectmanagement.database.ProjectDatabase
import com.ganeshgfx.projectmanagement.models.Project
import com.ganeshgfx.projectmanagement.models.ProjectWithTasks

class ProjectRepository(
    private val dao: ProjectDAO
) {

    val projectWithTasksFlow = dao.getProjectWithTasksFlow()

    suspend fun addProject(project: Project) {
        dao.insertProject(project)
    }

    suspend fun deleteProject(id: Long){
        dao.deleteProject(id)
    }

    suspend fun deleteAllProjects() {
        dao.deleteAllProjects()
    }
}