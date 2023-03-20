package com.ganeshgfx.projectmanagement.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.models.Project
import com.ganeshgfx.projectmanagement.models.ProjectTaskCount
import com.ganeshgfx.projectmanagement.models.ProjectWithTasks
import com.ganeshgfx.projectmanagement.models.Status
import com.ganeshgfx.projectmanagement.repositories.ProjectRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProjectOverviewViewModel(private val repo: ProjectRepository) : ViewModel() {
    private var _currentProjectId = -1L
    private var tasksJob: Job? = null
    val taskStatusCount = MutableLiveData(emptyList<ProjectTaskCount>())
    val pendingTasks = MutableLiveData(0)
    val doingTasks = MutableLiveData(0)
    val doneTasks = MutableLiveData(0)
    fun getTasksStatus(projectId: Long) {
        _currentProjectId = projectId
        if (tasksJob != null)
            tasksJob!!.cancel()
        tasksJob = viewModelScope.launch {
            repo.tasksStatusFlow(_currentProjectId).collect {
                pendingTasks.postValue(getStatusCount(it, Status.PENDING))
                doingTasks.postValue(getStatusCount(it, Status.IN_PROGRESS))
                doneTasks.postValue(getStatusCount(it, Status.DONE))
                taskStatusCount.postValue(it)
            }
        }
    }

    fun getStatusCount(it: List<ProjectTaskCount>, status: Status) =
        it.let {
            var temp = 0
            it.filter { it.status == status }
                .map { temp += it.count }
            temp
        }
}