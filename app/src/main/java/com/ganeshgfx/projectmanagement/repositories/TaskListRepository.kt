package com.ganeshgfx.projectmanagement.repositories


import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.database.FirestoreHelper
import com.ganeshgfx.projectmanagement.database.ProjectDAO
import com.ganeshgfx.projectmanagement.database.TaskDAO
import com.ganeshgfx.projectmanagement.models.Status
import com.ganeshgfx.projectmanagement.models.Task
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentChange.*
import com.google.firebase.firestore.DocumentChange.Type.*
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class TaskListRepository @Inject constructor(
    private val dao: TaskDAO,
    private val remote: FirestoreHelper,
    private val scope: CoroutineScope
) {

    private var getTaskJob: Job? = null

    fun tasksFlow(
        _projectId: String,
        status: List<Status> = listOf(Status.DONE, Status.PENDING, Status.IN_PROGRESS)
    ): Flow<List<Task>> {
        scope.launch(Dispatchers.IO) {
            remote.getTasks(_projectId).onEach { list ->
                list.forEach {
                    val task = it.document.toObject<Task>()
                    when(it.type){
                        ADDED ->{
                            dao.insertTask(task)
                        }
                        MODIFIED ->{
                            dao.updateTask(task)
                        }
                        REMOVED ->{
                            //dao.deleteTask(task)
                        }
                    }
                }
            }.launchIn(this)
        }
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
}