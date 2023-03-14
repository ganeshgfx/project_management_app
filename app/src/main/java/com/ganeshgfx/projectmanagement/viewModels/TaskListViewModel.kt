package com.ganeshgfx.projectmanagement.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.models.Status
import com.ganeshgfx.projectmanagement.models.Task
import com.ganeshgfx.projectmanagement.repositories.TaskListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskListViewModel(private val repository: TaskListRepository) : ViewModel() {

    val tasks get() = repository.tasks
    val showForm = MutableLiveData(false)

    val title = MutableLiveData("")
    val titleError = MutableLiveData(false)
    val description = MutableLiveData("")
    val descriptionError = MutableLiveData(false)

    private var _currentProjectId = -1L

    fun setProjectId(projectId: Long) = viewModelScope.launch(Dispatchers.IO) {
        _currentProjectId = projectId
        repository.getTasks(projectId)
    }

    fun viewForm() {
        showForm.postValue(!showForm.value!!)
    }

    fun addTasks() = viewModelScope.launch(Dispatchers.IO) {
        val title: String = title.value!!
        val description: String = description.value!!
        when {
            title.isBlank() -> titleError.postValue(true)
            description.isBlank() -> descriptionError.postValue(true)
            else -> {
                repository.addTask(
                    Task(
                        projectId = _currentProjectId,
                        title = title,
                        description = description,
                        status = Status.PENDING
                    )
                )
                viewForm()
            }
        }
    }

    suspend fun updateTask(task: Task):Int{
        val result = repository.updateTask(task)
        return result
    }
}