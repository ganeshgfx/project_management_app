package com.ganeshgfx.projectmanagement.repositories

import androidx.lifecycle.MutableLiveData
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.database.FirestoreHelper
import com.ganeshgfx.projectmanagement.database.ProjectDAO
import com.ganeshgfx.projectmanagement.models.Project
import com.ganeshgfx.projectmanagement.models.ProjectWithTasks
import com.google.firebase.FirebaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProjectRepository @Inject constructor(
    private val dao: ProjectDAO,
    private val remote: FirestoreHelper
) {

    //TODO specify with ID
    suspend fun projectWithTasksFlow(): Flow<List<ProjectWithTasks>> {
        try {
            val result = remote.getOwnProjects().get().await().documents
            log(result.size)
        }catch (error:FirebaseException){
            log(error)
        }
        return dao.getProjectWithTasksFlow()
    }

    //for getting individual project
    fun tasksStatusFlow(_projectId: String) = dao.tasksStatus(_projectId)

    val addingProject = MutableLiveData(false)

    suspend fun addProject(title:String, description:String) {
        addingProject.postValue(true)
        try {
            val response = remote.addProject(
                Project(title = title, description = description)
            )
            dao.insertProject(response)
            addingProject.postValue(false)
        }catch(e: FirebaseException) {
            log("Error adding project : ", e)
        }
    }

    fun getProject(id:String) = dao.getProject(id)

    suspend fun deleteProject(id: String) {
        dao.deleteProject(id)
    }

    suspend fun deleteAllProjects() {
        dao.deleteAllProjects()
    }


}