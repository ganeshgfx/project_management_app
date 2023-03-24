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
import kotlin.system.measureTimeMillis

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
    var date : Long? = null
    val dateString = MutableLiveData("")

    val filterOptionsVisibility = MutableLiveData(true)
    val menuListener = Toolbar.OnMenuItemClickListener {
        filterOptionsVisibility.postValue(!filterOptionsVisibility.value!!)
        true
    }

    fun setFilters(status : List<Status>){
        filters = status
        getTasks(_currentProjectId)
    }

    private var tasksFlowJob : Job? = null
    fun getTasks(projectId: String) {
        _currentProjectId = projectId
        tasksFlowJob?.cancel()
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
        val _title: String = title.value!!
        val _description: String = description.value!!
        when {
            _title.isBlank() -> titleError.postValue(true)
            _description.isBlank() -> descriptionError.postValue(true)
            else -> {
                val result = repository.addTask(
                    Task(
                        projectId = _currentProjectId,
                        title = _title,
                        description = _description,
                        status = Status.PENDING,
                        dueDate = date
                    )
                )
                viewForm()
                if(result!=null){
                    title.postValue("")
                    description.postValue("")
                    titleError.postValue(false)
                    descriptionError.postValue(false)
                    clearDueDate()
                }
            }
        }
    }

    suspend fun updateTask(task: Task):Int{
        val result = repository.updateTask(task)
        return result
    }

    fun clearDueDate(){
        date = null
        dateString.postValue("")
    }
}