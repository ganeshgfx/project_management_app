package com.ganeshgfx.projectmanagement.repositories

import com.ganeshgfx.projectmanagement.Utils.dateString
import com.ganeshgfx.projectmanagement.api.OpenAiHelper
import com.ganeshgfx.projectmanagement.models.gpt.Message
import com.ganeshgfx.projectmanagement.database.FirestoreHelper
import com.ganeshgfx.projectmanagement.database.ProjectDAO
import com.ganeshgfx.projectmanagement.database.TaskDAO
import com.ganeshgfx.projectmanagement.database.UserDAO
import com.ganeshgfx.projectmanagement.models.Chat
import com.ganeshgfx.projectmanagement.models.gpt.Information
import javax.inject.Inject

const val PERSONA =
    "The following is a conversation with an AI assistant. The assistant is helpful, creative, clever, and very friendly ,you know following information, give short replies,If there too much information reduce it to only most relevant information, Only replies based on provided information, don't show special character at the beginning of reply."

class ChatRepository @Inject constructor(
    private val service: OpenAiHelper,
    private val remote: FirestoreHelper,
    private val usersDAO: UserDAO,
    private val projectDAO: ProjectDAO,
    private val taskDAO: TaskDAO,
) {
    private var _isBusy = false
    val isBusy get() = _isBusy

    private var _projectId: String = ""

    private var chatHistory = emptyList<Message>()

    suspend fun chat(msg: String, projectId: String): Chat? {
        _isBusy = true

        if (_projectId != projectId) {
            _projectId = projectId
            chatHistory = listOf(
                Message("system", getProjectContext(projectId)),
                Message("user", msg)
            )
        } else {
            chatHistory = chatHistory + Message("user", msg)
        }
        val requestChat = service.requestChat(chatHistory)
        chatHistory = chatHistory + requestChat
        val chat = Chat(requestChat.last().content, false)

        _isBusy = false
        return chat
    }

    private suspend fun getProjectContext(projectId: String): String {
        val project = projectDAO.getProjectData(projectId)
        val users = usersDAO.getMembersList(projectId).map {
            it.displayName
        }
        val owner = usersDAO.getUser(project.uid)
        val tasks = taskDAO.getTasks(projectId).map {
            val taskInfo = mutableListOf<String>()
            taskInfo.add("title : ${it.title}")
            taskInfo.add("description : ${it.description}")
            //            taskInfo.add("assignedTo : ${it.assignedTo}")
            taskInfo.add("startDate : ${it.startDate?.let { it1 -> dateString(it1) }}")
            taskInfo.add("endDate : ${it.endDate?.let { it1 -> dateString(it1) }}")
            taskInfo.add("status : ${it.status}")
            taskInfo.toString()
        }
        val projectInfo = Information(
            projectInfo = listOf(
                "Title : ${project.title}",
                "Description : ${project.description}",
                "Created by : ${owner.displayName}"
            ),
            currently_logged_user = usersDAO.getUser(remote.myUid!!).let { it.displayName },
            userList = users,
            taskCount = tasks.size,
            taskList = tasks
        )

        val prompt =
            "$PERSONA project information : $projectInfo"
        return prompt
    }

}