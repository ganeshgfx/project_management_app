package com.ganeshgfx.projectmanagement.repositories

import androidx.lifecycle.MutableLiveData
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.database.FirestoreHelper
import com.ganeshgfx.projectmanagement.database.ProjectDAO
import com.ganeshgfx.projectmanagement.models.Project
import com.google.firebase.auth.FirebaseAuth
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

private const val USERS = "users"
private const val PROJECTS = "projects"

class ProjectRepository @Inject constructor(
    private val dao: ProjectDAO,
    private val remote: FirebaseFirestore
) {
    val projectWithTasksFlow = dao.getProjectWithTasksFlow()

    //for getting individual project
    fun tasksStatusFlow(_projectId: String) = dao.tasksStatus(_projectId)

    val addingProject = MutableLiveData(false)

    suspend fun addProject(project: Project) {
        addingProject.postValue(true)
        try {
            val ref = remote.collection(PROJECTS).document(project.uid).collection(PROJECTS)
            val id = ref.document().id
            val data = project.let {
                Project(id = id, uid = it.uid, title = it.title, description = it.description)
            }
            ref.add(data).await()
            dao.insertProject(data)
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

    fun getLoggedUser(): String {
        val user: String = FirebaseAuth.getInstance().currentUser?.uid!!
        return user
    }
}

inline fun <reified T> CollectionReference.getData() = callbackFlow {
    val listener =
        EventListener<QuerySnapshot> { list, error ->
            //TODO HANDLE ERROR !
            trySend(list!!.map { it.toObject<T>() })
        }
    val registration = addSnapshotListener(listener)
    awaitClose { registration.remove() }
}