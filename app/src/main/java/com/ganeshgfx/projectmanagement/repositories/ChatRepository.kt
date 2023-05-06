package com.ganeshgfx.projectmanagement.repositories

import com.ganeshgfx.projectmanagement.Utils.dateString
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.Utils.randomString
import com.ganeshgfx.projectmanagement.api.AIService
import com.ganeshgfx.projectmanagement.models.gpt.ChatRequest
import com.ganeshgfx.projectmanagement.models.gpt.Message
import com.ganeshgfx.projectmanagement.database.FirestoreHelper
import com.ganeshgfx.projectmanagement.database.ProjectDAO
import com.ganeshgfx.projectmanagement.database.TaskDAO
import com.ganeshgfx.projectmanagement.database.UserDAO
import com.ganeshgfx.projectmanagement.models.Chat
import com.ganeshgfx.projectmanagement.models.gpt.GptRequest
import com.ganeshgfx.projectmanagement.models.gpt.Information
import com.google.gson.Gson
import kotlinx.coroutines.delay
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val service: AIService,
    private val remote: FirestoreHelper,
    private val usersDAO: UserDAO,
    private val projectDAO: ProjectDAO,
    private val taskDAO: TaskDAO,
) {
    private var _isBusy = false
    val isBusy get() = _isBusy

    private var _projectId: String = ""

    var chatHistory = emptyList<Message>()

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
        val chat = requestChat( chatHistory)

//        delay(100)
//        val chat = Chat(randomString(100), false)

        _isBusy = false
        return chat
    }

    suspend fun requestChat(messages: List<Message>): Chat? {
        val request = ChatRequest(
            model = "gpt-3.5-turbo",
            messages = messages,
            presence_penalty = 0.6,
            stream = false,
            temperature = 0.9
        )
        val response = service.getChatCompletion(request)
        if (response.isSuccessful) {

            chatHistory = chatHistory + response.body()?.choices?.map {
                it.message
            }!!

           // val gson = Gson()

           // log(gson.toJson(chatHistory).toString())

            return response.body()?.choices?.last()?.let {
                Chat(it.message.content, false)
            }
        } else {
            val responseBody = response.errorBody()
            val responseText = responseBody?.let { String(it.bytes(), Charsets.UTF_8) }
            log("RETROFIT_ERROR", response.code().toString(), responseText.toString())
            return null
        }
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
            "The following is a conversation with an AI assistant. The assistant is helpful, creative, clever, and very friendly ,you know following information, give short replies,If there too much information reduce it to only most relevant information, Only replies based on provided information, don't show special character at the beginning of reply. project information : $projectInfo"
        return prompt
    }

}