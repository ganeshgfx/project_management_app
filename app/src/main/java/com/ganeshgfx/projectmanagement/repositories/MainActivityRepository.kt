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

    private fun runGetProjectInfo(projectIds: Set<String>) =
        remote.getProjectList(projectIds.toList())
            .catch { error ->
                log(error.cause.toString())
            }
            .onEach { list ->
                list.documentChanges.forEach { change ->
                    val project: Project = change.document.toObject()
                    when (change.type) {
                        DocumentChange.Type.ADDED -> projectDAO.insertProject(project)
                        DocumentChange.Type.MODIFIED -> projectDAO.updateProject(project)
                        DocumentChange.Type.REMOVED -> projectDAO.deleteProject(project.id)
                    }
                }
            }
            .launchIn(scope)

    private fun runGetTasks(projectIds: Set<String>) =
        remote.getAllTasks(projectIds.toList())
            .catch { error ->
            log(error.cause.toString())
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
                log(error.cause.toString())
            }
            .onEach { snapshot ->
                val memberList = mutableListOf<Member>()
                snapshot.documentChanges.forEach {
                    val member = Member(
                        projectId = it.document["projectId"].toString(),
                        uid = it.document["userId"].toString()
                    )
                    when (it.type) {
                        DocumentChange.Type.ADDED -> {
                            if (remote.myUid != member.uid) {
                                memberList.add(member)
                            }
                        }
                        DocumentChange.Type.MODIFIED -> {}
                        DocumentChange.Type.REMOVED -> userDAO.deleteMember(member)
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
                            log(error)
                        }
                    }
                }
            }
            .launchIn(scope)
}