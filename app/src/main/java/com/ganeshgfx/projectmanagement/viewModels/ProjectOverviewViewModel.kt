package com.ganeshgfx.projectmanagement.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.models.Project
import com.ganeshgfx.projectmanagement.models.ProjectTaskCount
import com.ganeshgfx.projectmanagement.models.Status
import com.ganeshgfx.projectmanagement.repositories.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectOverviewViewModel @Inject constructor(private val repo: ProjectRepository) :
    ViewModel() {
    private var _currentProjectId = ""
    val currentProjectId get() = _currentProjectId

    val myUid = repo.myUid

    private var projectJob: Job? = null
    private var taskCountJob: Job? = null

    val taskStatusCount = MutableLiveData(emptyList<ProjectTaskCount>())
    val pendingTasks = MutableLiveData(0)
    val doingTasks = MutableLiveData(0)
    val doneTasks = MutableLiveData(0)

    private val _project = MutableLiveData<Project>()
    val project: LiveData<Project> get() = _project

    fun setCurrentProject(projectId: String){
        _currentProjectId = projectId
    }

    fun getProjectInfo() {
        if (projectJob != null) projectJob!!.cancel()
        projectJob = viewModelScope.launch {
            repo.getProject(currentProjectId).collect {
                _project.postValue(it)
            }
        }

        if (taskCountJob != null) taskCountJob!!.cancel()
        taskCountJob = viewModelScope.launch {
            repo.tasksStatusFlow(currentProjectId).collect {
                pendingTasks.postValue(getStatusCount(it, Status.PENDING))
                doingTasks.postValue(getStatusCount(it, Status.IN_PROGRESS))
                doneTasks.postValue(getStatusCount(it, Status.DONE))
                taskStatusCount.postValue(it)
            }
        }
    }

    private fun getStatusCount(it: List<ProjectTaskCount>, status: Status) =
        it.let {
            var temp = 0
            it.filter { it.status == status }
                .map { temp += it.count }
            temp
        }

    fun deleteProject() = viewModelScope.launch {
        val deleted = repo.deleteProject(currentProjectId) == 1
        if(deleted){
            _currentProjectId = ""
        }
    }
}