package com.ganeshgfx.projectmanagement.viewModels

import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.adapters.TaskListRecyclerViewAdapter
import com.ganeshgfx.projectmanagement.models.Status
import com.ganeshgfx.projectmanagement.models.Task
import com.ganeshgfx.projectmanagement.repositories.TaskListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskListViewModel(private val repository: TaskListRepository) : ViewModel() {

    private var _currentProjectId =-1L

    private var filters = listOf(Status.DONE,Status.IN_PROGRESS,Status.PENDING)
    private val _tasks = MutableLiveData<List<Task>>()
    val tasks:LiveData<List<Task>> get() = _tasks

    val taskListAdapter = TaskListRecyclerViewAdapter()

    val showForm = MutableLiveData(false)
    val title = MutableLiveData("")
    val titleError = MutableLiveData(false)
    val description = MutableLiveData("")
    val descriptionError = MutableLiveData(false)

    val filterOptionsVisibility = MutableLiveData(true)
    val menuListener = Toolbar.OnMenuItemClickListener {
        filterOptionsVisibility.postValue(!filterOptionsVisibility.value!!)
        true
    }

    fun setFilters(status : List<Status>){
        filters = status
        getTasks(_currentProjectId)
    }

    private var tasksFlowJob = viewModelScope.launch {
        repository.tasksFlow(_currentProjectId).collect{
            _tasks.postValue(it)
            taskListAdapter.setData(it)
        }
    }
    fun getTasks(projectId: Long) {
        _currentProjectId = projectId
        tasksFlowJob.cancel()
        tasksFlowJob = viewModelScope.launch {
            repository.tasksFlow(_currentProjectId,filters).collect {
                _tasks.postValue(it)
                taskListAdapter.setData(it)
            }
        }
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