package com.ganeshgfx.projectmanagement.repositories


import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.Utils.randomString
import com.ganeshgfx.projectmanagement.database.ProjectDatabase
import com.ganeshgfx.projectmanagement.models.Status
import com.ganeshgfx.projectmanagement.models.Task
import kotlinx.coroutines.GlobalScope

class TaskListRepository(
    private val db: ProjectDatabase
){
    val tasks = MutableLiveData<List<Task>>()

    suspend fun getTasks(projectId : Long){
        val taskList = db.projectDao().getAllTasks(projectId)
        tasks.postValue(taskList)
    }

    suspend fun addTask(task: Task){
        db.projectDao().insertTask(task)
        getTasks(task.projectId)
    }

    suspend fun updateTask(task: Task):Int{
        val result = db.projectDao().updateTask(task)
        //getTasks(task.projectId)
        return result
    }
}