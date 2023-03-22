package com.ganeshgfx.projectmanagement.repositories

import com.ganeshgfx.projectmanagement.database.FirestoreHelper
import com.ganeshgfx.projectmanagement.database.ProjectDAO
import com.ganeshgfx.projectmanagement.models.Project
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class ProjectRepository @Inject constructor(
    private val dao: ProjectDAO,
    private val helper: FirestoreHelper
) {
    val projectWithTasksFlow = dao.getProjectWithTasksFlow()

    fun tasksStatusFlow(_projectId: Long) = dao.tasksStatus(_projectId)

    suspend fun addProject(project: Project) {
        //TODO Validate Insertion
        helper.addProject(project)
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