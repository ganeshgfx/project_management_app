package com.ganeshgfx.projectmanagement.repositories


import com.ganeshgfx.projectmanagement.database.ProjectDAO
import com.ganeshgfx.projectmanagement.models.Status
import com.ganeshgfx.projectmanagement.models.Task

class TaskListRepository(
    private val dao: ProjectDAO
) {
    fun tasksFlow(
        _projectId: Long,
        status: List<Status> = listOf(Status.DONE, Status.PENDING, Status.IN_PROGRESS)
    ) = dao.getTasksFlow(_projectId, status)

    suspend fun addTask(task: Task) {
        dao.insertTask(task)
    }

    suspend fun updateTask(task: Task): Int {
        return dao.updateTask(task)
    }
}