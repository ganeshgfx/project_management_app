package com.ganeshgfx.projectmanagement.repositories


import androidx.lifecycle.MutableLiveData
import com.ganeshgfx.projectmanagement.Utils.randomString
import com.ganeshgfx.projectmanagement.database.ProjectDatabase
import com.ganeshgfx.projectmanagement.models.Status
import com.ganeshgfx.projectmanagement.models.Task
import kotlinx.coroutines.GlobalScope

class TaskListRepository(

) {
    val tasks = MutableLiveData<List<Task>>()
    init {
        getTasks(1)
    }
    fun getTasks(size:Int) : List<Task>{
        val status = listOf(Status.IN_PROGRESS, Status.DONE, Status.PENDING)
        val temp = mutableListOf<Task>()
        for (i in 0..size) {
            temp.add(
                Task(
                    id = i.toLong(),
                    title = randomString(10),
                    description = randomString(10),
                    status = status.random()
                )
            )
        }
        tasks.postValue(temp)
        return temp
    }
}