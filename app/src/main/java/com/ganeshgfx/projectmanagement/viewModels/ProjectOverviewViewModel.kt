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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    private val _membersCount  = MutableLiveData("9")
    val membersCount get() = _membersCount

    private val _project = MutableLiveData<Project>()
    val project: LiveData<Project> get() = _project

    fun setCurrentProject(projectId: String) {
        _currentProjectId = projectId
    }

    fun getProjectInfo() {

        projectJob?.cancel()
        projectJob = repo.getProject(currentProjectId).onEach {
            _project.postValue(it)
        }.launchIn(viewModelScope)

        taskCountJob?.cancel()
        taskCountJob = repo.tasksStatusFlow(currentProjectId).onEach {
            pendingTasks.postValue(getStatusCount(it, Status.PENDING))
            doingTasks.postValue(getStatusCount(it, Status.IN_PROGRESS))
            doneTasks.postValue(getStatusCount(it, Status.DONE))
            taskStatusCount.postValue(it)
        }.launchIn(viewModelScope)
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
        if (deleted) {
            _currentProjectId = ""
        }
    }
}