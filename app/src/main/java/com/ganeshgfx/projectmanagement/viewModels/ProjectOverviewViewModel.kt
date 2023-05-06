package com.ganeshgfx.projectmanagement.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.adapters.UserListAdapter
import com.ganeshgfx.projectmanagement.models.Project
import com.ganeshgfx.projectmanagement.models.ProjectTaskCount
import com.ganeshgfx.projectmanagement.models.Status
import com.ganeshgfx.projectmanagement.repositories.ProjectRepository
import com.ganeshgfx.projectmanagement.repositories.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectOverviewViewModel @Inject constructor(
    private val repo: ProjectRepository,
    private val userRepo: UserRepo
) :
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

    private var _membersCount = MutableLiveData("0")
    val membersCount get() = _membersCount

    private val _project = MutableLiveData<Project>()
    val project: LiveData<Project> get() = _project

    private val _updateProject = MutableLiveData(false)
    val updateProject: LiveData<Boolean> get() = _updateProject

    val formProjectTitle = MutableLiveData("")
    val formProjectDescription = MutableLiveData("")

    val userListAdapter = UserListAdapter(false)

    private val _toggleViewMember = MutableLiveData(false)
    val toggleViewMember: LiveData<Boolean> get() = _toggleViewMember

    fun toggleViewMemberList(toggle: Boolean) {
        with(_toggleViewMember) {
            postValue(toggle)
        }
    }

    fun toggleEdit() {
        with(_updateProject) {
            postValue(value!!.not())
        }
    }
    private var getProjectMembersJob: Job? = null
    private fun loadUsers() {
        getProjectMembersJob?.cancel()
        getProjectMembersJob = viewModelScope.launch {
            userRepo.getProjectMembers(_currentProjectId).collect{
                userListAdapter.setData(it)
            }
        }
    }

    fun updateProject() = viewModelScope.launch {
        val validate = validateProjectForm()
        if (validate) {
            val title: String = formProjectTitle.value!!
            val description: String = formProjectDescription.value!!
            repo.updateProject(currentProjectId, title, description)
            // formProjectTitle.postValue("")
            // formProjectDescription.postValue("")
            log(title, description)
            toggleEdit()
        }
    }

    fun validateProjectForm(): Boolean {
        val title: String = formProjectTitle.value!!
        val description: String = formProjectDescription.value!!
        return title.isNotBlank() && description.isNotBlank()
    }

    val isMyProject = MutableLiveData(false)

    fun setCurrentProject(projectId: String) = viewModelScope.launch {
        _currentProjectId = projectId
        val project = repo.getProjectInfo(projectId)
        isMyProject.postValue(repo.myUid == project.uid)
        formProjectTitle.postValue(project.title)
        formProjectDescription.postValue(project.description)
        repo.getMemberCount(projectId).onEach {
            _membersCount.postValue(it.toString())
        }.launchIn(viewModelScope)
        loadUsers()
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