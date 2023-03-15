package com.ganeshgfx.projectmanagement.repositories


import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.Utils.randomString
import com.ganeshgfx.projectmanagement.database.ProjectDAO
import com.ganeshgfx.projectmanagement.database.ProjectDatabase
import com.ganeshgfx.projectmanagement.models.Status
import com.ganeshgfx.projectmanagement.models.Task
import kotlinx.coroutines.GlobalScope

class TaskListRepository(
    private val dao: ProjectDAO
){
    fun tasksFlow(_projectId:Long) = dao.getTasksFlow(_projectId)

    suspend fun addTask(task: Task){
        dao.insertTask(task)
    }

    suspend fun updateTask(task: Task):Int{
        val result = dao.updateTask(task)
        return result
    }
}