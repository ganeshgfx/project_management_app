package com.ganeshgfx.projectmanagement.repositories

import androidx.lifecycle.MutableLiveData
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.database.FirestoreHelper
import com.ganeshgfx.projectmanagement.database.ProjectDAO
import com.ganeshgfx.projectmanagement.database.UserDAO
import com.ganeshgfx.projectmanagement.models.Project
import com.ganeshgfx.projectmanagement.models.ProjectWithTasks
import com.google.firebase.FirebaseException
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProjectRepository @Inject constructor(
    private val projectDAO: ProjectDAO,
    private val userDAO: UserDAO,
    private val remote: FirestoreHelper
) {
    fun projectWithTasksFlow(): Flow<List<ProjectWithTasks>> {
        return projectDAO.getProjectWithTasksFlow()
    }

    val myUid = remote.myUid

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

    fun getMemberCount(id: String) : Flow<Int> = userDAO.getMemberCount(id)

    suspend fun getProjectInfo(id: String) = projectDAO.getProjectInfo(id)

    suspend fun updateProject(id: String, title: String, description: String) {
        try {
            myUid?.let {
                remote.updateProject(
                    project = Project(
                        id = id,
                        title = title,
                        description = description,
                        uid = myUid
                    )
                )
            }
        } catch (e: FirebaseException) {
            log("at updateProject", "Error updating project : ", e)
        }
    }

    suspend fun deleteProject(id: String): Int {
        return try {
            remote.deleteProject(id)
            projectDAO.deleteProject(id)
        } catch (error: FirebaseException) {
            log(error)
            -1
        }
    }


}