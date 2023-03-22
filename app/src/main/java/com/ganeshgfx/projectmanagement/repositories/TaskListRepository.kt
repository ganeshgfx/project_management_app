package com.ganeshgfx.projectmanagement.repositories


import com.ganeshgfx.projectmanagement.database.ProjectDAO
import com.ganeshgfx.projectmanagement.database.TaskDAO
import com.ganeshgfx.projectmanagement.models.Status
import com.ganeshgfx.projectmanagement.models.Task
import javax.inject.Inject

class TaskListRepository @Inject constructor(
    private val dao: TaskDAO
) {
    fun tasksFlow(
        _projectId: Long,
        status: List<Status> = listOf(Status.DONE, Status.PENDING, Status.IN_PROGRESS)
    ) = dao.getTasksFlow(_projectId, status)

    suspend fun addTask(task: Task):Long {
        return dao.insertTask(task)
    }

    suspend fun updateTask(task: Task): Int {
        return dao.updateTask(task)
    }
}