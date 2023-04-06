package com.ganeshgfx.projectmanagement.repositories

import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.database.FirestoreHelper
import com.ganeshgfx.projectmanagement.database.ProjectDAO
import com.ganeshgfx.projectmanagement.database.TaskDAO
import com.ganeshgfx.projectmanagement.database.UserDAO
import com.ganeshgfx.projectmanagement.models.Member
import com.ganeshgfx.projectmanagement.models.Project
import com.ganeshgfx.projectmanagement.models.Task
import com.ganeshgfx.projectmanagement.models.User
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivityRepository @Inject constructor(
    private val userDAO: UserDAO,
    private val projectDAO: ProjectDAO,
    private val taskDAO: TaskDAO,
    private val remote: FirestoreHelper,
    private val scope: CoroutineScope
) {
    private var getProjects: Job? = null
    private var getProjectsInfo: Job? = null
    private var getTasks: Job? = null

    init {
        getProjects?.cancel()
        getProjects = remote.getProjectImIn()
            .catch { error ->
                log(error.cause.toString())
            }
            .onEach { list ->
                val projectIds = mutableSetOf<String>()
                list.documentChanges.forEach {
                    val pid = it.document.data["projectId"].toString()
                    val uid = it.document.data["userId"].toString()
                    val member = Member(projectId = pid, uid = uid)
                    when (it.type) {
                        DocumentChange.Type.ADDED -> {
                            if (uid != remote.myUid) {
                                userDAO.insertUser(User(uid = member.uid))
                            }
                            projectIds.add(pid)
                        }
                        DocumentChange.Type.MODIFIED -> {}
                        DocumentChange.Type.REMOVED -> {
                            projectDAO.deleteProject(member.projectId)
                        }
                    }
                }
                if (projectIds.isNotEmpty()) {
                    getProjectsInfo?.cancel()
                    getProjectsInfo = remote.getProjectList(projectIds.toList())
                        .catch { error ->
                            log(error.cause.toString())
                        }.onEach { list ->
                            list.documentChanges.forEach { change ->
                                val project: Project = change.document.toObject()
                                when (change.type) {
                                    DocumentChange.Type.ADDED -> {
                                        projectDAO.insertProject(project)
                                    }
                                    DocumentChange.Type.MODIFIED -> {
                                        projectDAO.updateProject(project)
                                    }
                                    DocumentChange.Type.REMOVED -> {
                                        projectDAO.deleteProject(project.id)
                                    }
                                }
                            }
                        }.launchIn(scope)
                    getTasks?.cancel()
                    getTasks = remote.getAllTasks(projectIds.toList()).catch { error ->
                        log(error.cause.toString())
                    }.onEach { list ->
                        val tasks = mutableListOf<Task>()
                        list.documentChanges.forEach {
                            val task = it.document.toObject<Task>()
                            //log(task,it.type)
                            when (it.type) {
                                DocumentChange.Type.ADDED -> {
                                    taskDAO.insertTask(task)
                                }
                                DocumentChange.Type.MODIFIED -> {
                                    taskDAO.updateTask(task)
                                }
                                DocumentChange.Type.REMOVED -> {
                                    taskDAO.deleteTask(task)
                                }
                            }
                        }
                    }.launchIn(scope)
                }
            }
            .launchIn(scope)
    }
}