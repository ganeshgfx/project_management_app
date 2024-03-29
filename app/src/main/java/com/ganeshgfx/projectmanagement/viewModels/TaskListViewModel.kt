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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(private val repository: TaskListRepository) : ViewModel() {

    private var _currentProjectId = ""

    private var filters = listOf(Status.DONE,Status.IN_PROGRESS,Status.PENDING)
    private val _tasks = MutableLiveData<List<Task>>()
    val tasks:LiveData<List<Task>> get() = _tasks

    val taskListAdapter = TaskListRecyclerViewAdapter()

    val showForm = MutableLiveData(false)
    val title = MutableLiveData("")
    val titleError = MutableLiveData(false)
    val description = MutableLiveData("")
    val descriptionError = MutableLiveData(false)
    var startDate : Long? = null
    var endDate : Long? = null
    val dateString = MutableLiveData("")
    var taskId = ""

    var selectedTask: Task? = null

    val filterOptionsVisibility = MutableLiveData(true)
    val menuListener = Toolbar.OnMenuItemClickListener {
        filterOptionsVisibility.postValue(!filterOptionsVisibility.value!!)
        true
    }

    fun setFilters(status : List<Status>){
        filters = status
        getTasks(_currentProjectId)
    }

    private var updateTask : Task? = null
    private var tasksFlowJob : Job? = null
    fun getTasks(projectId: String) {
        _currentProjectId = projectId
        tasksFlowJob?.cancel()
        tasksFlowJob = viewModelScope.launch {
            repository.tasksFlow(_currentProjectId,filters).collect { taskList ->
                _tasks.postValue(taskList)
                //log(taskList.map { taskList.status })
                taskListAdapter.setData(taskList)
                updateTask?.let {
                    taskListAdapter.updateData(it)
                    updateTask = null
                }
            }
        }
    }

    fun viewForm() {
        showForm.postValue(!showForm.value!!)
    }



    fun sendTasks() = viewModelScope.launch(Dispatchers.IO) {
        val _title: String = title.value!!
        val _description: String = description.value!!
        when {
            _title.isBlank() -> titleError.postValue(true)
            _description.isBlank() -> descriptionError.postValue(true)
            else -> {
                val status = selectedTask?.status ?: Status.PENDING
                log(status,selectedTask.toString())
                val result = repository.addTask(
                    Task(
                        id = taskId,
                        projectId = _currentProjectId,
                        title = _title,
                        description = _description,
                        status = status,
                        startDate = startDate,
                        endDate = endDate
                    )
                )
                viewForm()
                if(result!=null){
                    clearInputs()
                }
            }
        }
    }

    fun clearInputs() {
        taskId = ""
        title.postValue("")
        description.postValue("")
        titleError.postValue(false)
        descriptionError.postValue(false)
        selectedTask = null
        clearDate()
    }

    suspend fun updateTask(task: Task):Long{
        val result = repository.updateTask(task)
        updateTask = task
        selectedTask = null
        return result
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.deleteTask(task)
        selectedTask = null
    }

    fun clearDate(){
        startDate = null
        endDate = null
        dateString.postValue("")
        //selectedTask = null
    }
}