package com.ganeshgfx.projectmanagement.repositories

import androidx.lifecycle.MutableLiveData
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.Utils.randomString
import com.ganeshgfx.projectmanagement.database.FirestoreHelper
import com.ganeshgfx.projectmanagement.database.ProjectDAO
import com.ganeshgfx.projectmanagement.database.UserDAO
import com.ganeshgfx.projectmanagement.models.Member
import com.ganeshgfx.projectmanagement.models.Project
import com.ganeshgfx.projectmanagement.models.ProjectWithTasks
import com.ganeshgfx.projectmanagement.models.User
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.DocumentChange.Type.*
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class ProjectRepository @Inject constructor(
    private val projectDAO: ProjectDAO,
    private val remote: FirestoreHelper
) {
    fun projectWithTasksFlow(): Flow<List<ProjectWithTasks>> {
        return projectDAO.getProjectWithTasksFlow()
    }

    val myUid = remote.myUid

    //for getting individual project
    fun tasksStatusFlow(_projectId: String) = projectDAO.tasksStatus(_projectId)

    val addingProject = MutableLiveData(false)

    suspend fun addProject(title: String, description: String) {
        addingProject.postValue(true)
        try {
            val response = remote.addProject(
                Project(title = title, description = description)
            )
            projectDAO.insertProject(response)
            addingProject.postValue(false)
        } catch (e: FirebaseException) {
            log("at addProject", "Error adding project : ", e)
        }
    }

    fun getProject(id: String) = projectDAO.getProject(id)

    suspend fun deleteProject(id: String): Int {
        try {
            remote.deleteProject(id)
            return projectDAO.deleteProject(id)
        } catch (error: FirebaseException) {
            log(error)
            return -1
        }
    }


}