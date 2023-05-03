package com.ganeshgfx.projectmanagement.repositories


import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.Utils.randomString
import com.ganeshgfx.projectmanagement.database.FirestoreHelper
import com.ganeshgfx.projectmanagement.database.ProjectDAO
import com.ganeshgfx.projectmanagement.database.TaskDAO
import com.ganeshgfx.projectmanagement.models.Status
import com.ganeshgfx.projectmanagement.models.Task
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentChange.*
import com.google.firebase.firestore.DocumentChange.Type.*
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class TaskListRepository @Inject constructor(
    private val dao: TaskDAO,
    private val remote: FirestoreHelper
) {
    fun tasksFlow(
        _projectId: String,
        status: List<Status> = listOf(Status.DONE, Status.PENDING, Status.IN_PROGRESS)
    ): Flow<List<Task>> {
        return dao.getTasksFlow(_projectId, status)
    }

    suspend fun addTask(task: Task): Long {
        val result = remote.addTask(task)
        return dao.insertTask(result)
    }

    suspend fun updateTask(task: Task): Int {
        remote.updateTask(task)
        return dao.updateTask(task)
    }
    suspend fun deleteTask(task: Task){
        remote.deleteTask(task)
    }

}