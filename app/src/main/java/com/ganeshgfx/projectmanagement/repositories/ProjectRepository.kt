package com.ganeshgfx.projectmanagement.repositories

import androidx.lifecycle.MutableLiveData
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.database.FirestoreHelper
import com.ganeshgfx.projectmanagement.database.ProjectDAO
import com.ganeshgfx.projectmanagement.models.Project
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProjectRepository @Inject constructor(
    private val dao: ProjectDAO,
    private val remote: FirestoreHelper
) {

    val projectWithTasksFlow = dao.getProjectWithTasksFlow()

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
        } catch (e: Exception) {
            log("Error adding project : ", e)
        }
    }

    suspend fun deleteProject(id: Long) {
        dao.deleteProject(id)
    }

    suspend fun deleteAllProjects() {
        dao.deleteAllProjects()
    }

    fun getLoggedUserId(): String {
        val user: String = FirebaseAuth.getInstance().currentUser?.uid!!
        return user
    }
}