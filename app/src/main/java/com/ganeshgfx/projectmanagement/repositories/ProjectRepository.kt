package com.ganeshgfx.projectmanagement.repositories

import androidx.lifecycle.MutableLiveData
import com.ganeshgfx.projectmanagement.Utils.log
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class ProjectRepository @Inject constructor(
    private val projectDAO: ProjectDAO,
    private val userDAO: UserDAO,
    private val remote: FirestoreHelper,
    private val scope: CoroutineScope
) {
    private var getProjectImInJob: Job? = null
    private var getProjectsJob: Job? = null
    suspend fun projectWithTasksFlow(): Flow<List<ProjectWithTasks>> {
        withContext(Dispatchers.IO) {
            getProjectImInJob?.cancel()

            scope.launch(Dispatchers.IO) {
                remote.getProjectImIn().onEach { list ->
                    val projectIds = mutableSetOf<String>()
                    val memberList = mutableListOf<Member>()
                    list.forEach {
                        val pid = it.document.data["projectId"].toString()
                        val uid = it.document.data["userId"].toString()
                        val member = Member(projectId = pid, uid = uid)
                        when (it.type) {
                            ADDED -> {
                                if(uid!=remote.myUid){
                                    userDAO.insertUser(User(uid = member.uid))
                                }
                                projectIds.add(pid)
                            }
                            MODIFIED -> {}
                            REMOVED -> {
                                projectDAO.deleteProject(member.projectId)
                            }
                        }
                    }

                    getProjectsJob?.cancel()
                    //log(projectIds)
                    if (projectIds.isNotEmpty()) {
                        getProjectsJob = remote.getProjectList(projectIds.toList()).onEach { list ->
                            list.forEach { change ->
                                val project: Project = change.document.toObject()
                                when (change.type) {
                                    ADDED -> {
                                        projectDAO.insertProject(project)
                                    }
                                    MODIFIED -> {
                                        projectDAO.updateProject(project)
                                    }
                                    REMOVED -> {
                                        projectDAO.deleteProject(project.id)
                                    }
                                }
                            }
                        }.launchIn(this)
                    }
                }.launchIn(this)
            }
        }
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
            log("at addProject","Error adding project : ", e)
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