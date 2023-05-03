package com.ganeshgfx.projectmanagement.repositories

import android.database.sqlite.SQLiteConstraintException
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.database.FirestoreHelper
import com.ganeshgfx.projectmanagement.database.ProjectDAO
import com.ganeshgfx.projectmanagement.database.TaskDAO
import com.ganeshgfx.projectmanagement.database.UserDAO
import com.ganeshgfx.projectmanagement.models.*
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentChange.Type
import com.google.firebase.firestore.DocumentChange.Type.ADDED
import com.google.firebase.firestore.DocumentChange.Type.MODIFIED
import com.google.firebase.firestore.DocumentChange.Type.REMOVED
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.job
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val userDAO: UserDAO,
    private val projectDAO: ProjectDAO,
    private val taskDAO: TaskDAO,
    private val remote: FirestoreHelper,
    private val scope: CoroutineScope
) {
    private var getUsers: Job? = null
    private var getProjects: Job? = null
    private var getProjectsInfo: Job? = null
    private var getTasks: Job? = null

    private val _notice = MutableSharedFlow<Notice>(replay = 2)
    val notification get() = _notice

    init {
        //    startJob()
    }

    fun startJob() {
        getProjects?.cancel()
        getProjects = remote.getProjectImIn()
            .catch { error ->
                //log(error.cause.toString())
                checkError("startJob", error)
            }
            .onEach { list ->
                val projectIds = mutableSetOf<String>()
                list.documentChanges.forEach {
                    val pid = it.document.data["projectId"].toString()
                    val uid = it.document.data["userId"].toString()
                    val member = Member(projectId = pid, uid = uid)
                    when (it.type) {
                        ADDED -> {
                            if (uid != remote.myUid)
                                userDAO.insertUser(User(uid = member.uid))
                            projectIds.add(pid)
                        }

                        MODIFIED -> {}
                        REMOVED -> projectDAO.deleteProject(member.projectId)
                    }
                }
                if (projectIds.isNotEmpty()) {
                    getProjectsInfo?.cancel()
                    getProjectsInfo = runGetProjectInfo(projectIds)

                    getTasks?.cancel()
                    getTasks = runGetTasks(projectIds)

                    getUsers?.cancel()
                    getUsers = runGetMembers(projectIds)
                }
            }
            .launchIn(scope)
    }

    //private var imAddedToProject = false
    private fun runGetProjectInfo(projectIds: Set<String>): Job {
        return remote.getProjectList(projectIds.toList())
            .catch { error ->
                checkError("runGetProjectInfo()", error)
            }
            .onEach { list ->
                list.documentChanges.forEach { change ->
                    val project: Project = change.document.toObject()
                    when (change.type) {
                        ADDED -> {
                            val result = projectDAO.checkProject(project.id)
                            //imAddedToProject = false
                            if (result == 0 && project.uid != remote.myUid) {
                                // imAddedToProject = true
                                notification.emit(
                                    Notice(
                                        "You added to ${project.title}",
                                        project.description
                                    )
                                )
                            }
                            projectDAO.insertProject(project)
                        }

                        MODIFIED -> projectDAO.updateProject(project)
                        REMOVED -> {
                            projectDAO.deleteProject(project.id)
                        }
                    }
                }
            }
            .launchIn(scope)
    }

    private fun checkError(msg: String, error: Throwable) {
        log(msg)
        when (error.cause) {
            is SQLiteConstraintException -> {
                // Handle SQLiteConstraintException error here
                log("SQLiteConstraintException error: ${error.message}", error)
            }

            else -> {
                // Handle other errors here
                log("Error: ${error.message}")
            }
        }
    }

    private fun runGetTasks(projectIds: Set<String>) =
        remote.getAllTasks(projectIds.toList())
            .catch { error ->
                checkError("runGetTasks", error)
                //log(error.cause.toString())
            }
            .onEach { list ->
                list.documentChanges.forEach {
                    val task = it.document.toObject<Task>()
                    when (it.type) {
                        ADDED -> {
                            val result = taskDAO.insertTask(task)
                            if (result > 0)
                                notify("Task Added", "${task.title}")
                        }

                        MODIFIED -> {
                            taskDAO.updateTask(task)
                            notify("Task Updated", "${task.title} : ${task.status}")
                        }

                        REMOVED -> {
                            taskDAO.deleteTask(task)
                            notify("Task Removed", "${task.title}")
                        }
                    }
                }
            }
            .launchIn(scope)

    private fun runGetMembers(projectIds: Set<String>) =
        remote.getProjectMembersAll(projectIds.toList())
            .catch { error ->
                checkError("runGetMembers", error)
            }
            .onEach { snapshot ->
                log("hre")
                val memberList = mutableListOf<Member>()
                snapshot.documentChanges.forEach {
                    val member = Member(
                        projectId = it.document["projectId"].toString(),
                        uid = it.document["userId"].toString()
                    )
                    val project = getProject(member.projectId)
                    when (it.type) {
                        ADDED -> {
                            //if (remote.myUid != member.uid) {
                            memberList.add(member)
                            //}
                        }

                        MODIFIED -> {}
                        REMOVED -> {
                            if (member.uid == remote.myUid)
                                notify(
                                    "Member removed",
                                    "You removed from project ${project.title}"
                                )
                            else notify(
                                "Member removed",
                                "${getUser(member.uid).displayName} removed from project ${project.title}"
                            )
                            userDAO.deleteMember(member)
                        }
                    }
                }
                if (memberList.size > 0) {
                    val users = remote.getUsers(memberList.map { it.uid })
                    users.forEach {
                        userDAO.insertUser(it!!)
                    }
                    memberList.forEach {
                        try {
                            val result = userDAO.addMember(it)
                            val user = getUser(it.uid)
                            val project = getProject(it.projectId)
                            if (result > 0) {
                                notify(
                                    "New Member Added",
                                    "${user.displayName} Added to  ${project.title} "
                                )
                            }
                        } catch (error: Exception) {
                            checkError("memberList.forEach", error)
                        }
                    }
                }
            }
            .launchIn(scope)

    private suspend fun notify(title: String, info: String) {
        notification.emit(Notice(title, info))
    }

    private suspend fun getProject(id: String): Project {
        val project = projectDAO.getProjectInfo(id)
        //log(project)
        return project
    }

    private suspend fun getUser(id: String): User {
        val user = userDAO.getUser(id)
        //log(project)
        return user
    }
}