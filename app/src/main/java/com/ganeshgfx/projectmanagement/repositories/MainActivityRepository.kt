package com.ganeshgfx.projectmanagement.repositories

import android.database.sqlite.SQLiteConstraintException
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.database.FirestoreHelper
import com.ganeshgfx.projectmanagement.database.ProjectDAO
import com.ganeshgfx.projectmanagement.database.TaskDAO
import com.ganeshgfx.projectmanagement.database.UserDAO
import com.ganeshgfx.projectmanagement.models.*
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class MainActivityRepository @Inject constructor(
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
                        DocumentChange.Type.ADDED -> {
                            if (uid != remote.myUid)
                                userDAO.insertUser(User(uid = member.uid))
                            projectIds.add(pid)
                        }

                        DocumentChange.Type.MODIFIED -> {}
                        DocumentChange.Type.REMOVED -> projectDAO.deleteProject(member.projectId)
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

    private fun runGetProjectInfo(projectIds: Set<String>): Job {
        return remote.getProjectList(projectIds.toList())
            .catch { error ->
                checkError("runGetProjectInfo()", error)
            }
            .onEach { list ->
                list.documentChanges.forEach { change ->
                    val project: Project = change.document.toObject()
                    when (change.type) {
                        DocumentChange.Type.ADDED -> {
                            val result = projectDAO.checkProject(project.id)
                            if (result == 0)
                                notification.emit(
                                    Notice(
                                        "You added to ${project.title}",
                                        project.description
                                    )
                                )
                            projectDAO.insertProject(project)
                        }

                        DocumentChange.Type.MODIFIED -> projectDAO.updateProject(project)
                        DocumentChange.Type.REMOVED -> {
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
                        DocumentChange.Type.ADDED -> taskDAO.insertTask(task)
                        DocumentChange.Type.MODIFIED -> taskDAO.updateTask(task)
                        DocumentChange.Type.REMOVED -> taskDAO.deleteTask(task)
                    }
                }
            }
            .launchIn(scope)

    private fun runGetMembers(projectIds: Set<String>) =
        remote.getProjectMembersAll(projectIds.toList())
            .catch { error ->
                // log(error.cause.toString())
                checkError("runGetMembers", error)
            }
            .onEach { snapshot ->
                val memberList = mutableListOf<Member>()
                snapshot.documentChanges.forEach {
                    val member = Member(
                        projectId = it.document["projectId"].toString(),
                        uid = it.document["userId"].toString()
                    )
                    //log(member)
                    //val project = getProject(member.projectId)
                    when (it.type) {
                        DocumentChange.Type.ADDED -> {
                            if (remote.myUid != member.uid) {
                                memberList.add(member)
                                notify("New Member Added", "New Member Added to ")
                            }
                        }

                        DocumentChange.Type.MODIFIED -> {}
                        DocumentChange.Type.REMOVED -> {
                            try {
                                if(member.uid == remote.myUid) {
                                    notify("You removed from project", "You removed from project ")
                                }else{
                                    notify("Member removed from project", "Member removed from project")
                                }
                                userDAO.deleteMember(member)
                            } catch (error: NullPointerException) {
                            }
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
                            userDAO.addMember(it)
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

    private fun getProject(id: String) =
        projectDAO.getProjectInfo(id)
}