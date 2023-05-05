package com.ganeshgfx.projectmanagement.repositories

import com.ganeshgfx.projectmanagement.Utils.dateString
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.api.AIService
import com.ganeshgfx.projectmanagement.database.FirestoreHelper
import com.ganeshgfx.projectmanagement.database.ProjectDAO
import com.ganeshgfx.projectmanagement.database.TaskDAO
import com.ganeshgfx.projectmanagement.database.UserDAO
import com.ganeshgfx.projectmanagement.models.Chat
import com.ganeshgfx.projectmanagement.models.gpt.GptRequest
import com.ganeshgfx.projectmanagement.models.gpt.Information
import javax.inject.Inject

private const val MODEL =
    "text-davinci-003"
private const val TEMP = 0.9
private const val TOKEN_SIZE = 150

class ChatRepository @Inject constructor(
    private val service: AIService,
    private val remote: FirestoreHelper,
    private val usersDAO: UserDAO,
    private val projectDAO: ProjectDAO,
    private val taskDAO: TaskDAO,
) {
    private var _isBusy = false
    val isBusy get() = _isBusy
    suspend fun chat(msg: String, projectId: String): Chat? {
        _isBusy = true

        val prompt = "${getProjectContext(projectId)} $msg"

        val chat = send(prompt)

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
            "Pretend that you are a assistant you know following information, give short replies,If there too much information reduce it to only most relevant information, Only replies based on provided information, don't show special character at the beginning of reply. project information : $projectInfo"
        return prompt
    }

    suspend fun send(prompt: String): Chat? {
        var chat: Chat? = null

        val request = GptRequest(
            model = MODEL,
            prompt = prompt,
            temperature = TEMP,
            max_tokens = TOKEN_SIZE,
            top_p = 1.0,
            frequency_penalty = 0.0,
            presence_penalty = 0.5
        )

        val response = service.getCompletion(request)
        if (response.isSuccessful) {
            log(response.raw())
            response.body()?.choices?.get(0)?.text?.let {
                log(it)
                chat = Chat(removeSpecialCharactersFromBeginning(it).trim(), false)
            }

        } else {
            log("RETROFIT_ERROR", response.code().toString())
            val responseBody = response.errorBody()
            val responseBytes = responseBody // get the response body as bytes
            val responseText = responseBytes?.let { String(it.bytes(), Charsets.UTF_8) }
            if (responseText != null) {
                log(responseText)
            }
            log(response.raw())
        }
        return chat
    }

    fun removeSpecialCharactersFromBeginning(inputString: String): String {
        var outputString = inputString.trim() // Remove any leading or trailing whitespace
        var i = 0
        while (i < outputString.length && !outputString[i].isLetterOrDigit()) {
            i++
        }
        return outputString.substring(i)
    }
}