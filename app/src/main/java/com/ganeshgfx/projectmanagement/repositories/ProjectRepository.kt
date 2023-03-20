package com.ganeshgfx.projectmanagement.repositories

import androidx.lifecycle.MutableLiveData
import com.ganeshgfx.projectmanagement.database.FirebaseHelper
import com.ganeshgfx.projectmanagement.database.ProjectDAO
import com.ganeshgfx.projectmanagement.database.ProjectDatabase
import com.ganeshgfx.projectmanagement.models.Project
import com.ganeshgfx.projectmanagement.models.ProjectWithTasks
import com.ganeshgfx.projectmanagement.models.Status
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ProjectRepository(
    private val dao: ProjectDAO,
) {

    val projectWithTasksFlow = dao.getProjectWithTasksFlow()

    fun tasksStatusFlow(_projectId: Long) = dao.tasksStatus(_projectId)

    suspend fun addProject(project: Project) {
        dao.insertProject(project)
    }

    suspend fun deleteProject(id: Long){
        dao.deleteProject(id)
    }

    suspend fun deleteAllProjects() {
        dao.deleteAllProjects()
    }

    fun getLoggedUser():String{
        val user : String = FirebaseAuth.getInstance().currentUser?.uid!!
        return user
    }
}