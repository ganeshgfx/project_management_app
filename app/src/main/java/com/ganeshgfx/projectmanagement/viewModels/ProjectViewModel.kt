package com.ganeshgfx.projectmanagement.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.models.Project
import com.ganeshgfx.projectmanagement.models.ProjectWithTasks
import com.ganeshgfx.projectmanagement.repositories.ProjectRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProjectViewModel(private val repo: ProjectRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            repo.projectWithTasksFlow.collect {
                _projectWithTasksFlow.postValue(it)
               // log(it)
            }
        }
    }

    private val _projectWithTasksFlow = MutableLiveData<List<ProjectWithTasks>>()
    val projectWithTasksFlow: LiveData<List<ProjectWithTasks>> get() = _projectWithTasksFlow

    val showForm = MutableLiveData(false)
    val formProjectTitle = MutableLiveData("")
    val formProjectTitleError = MutableLiveData(false)
    val formProjectDescription = MutableLiveData("")
    val formProjectDescriptionError = MutableLiveData(false)

    private fun addProject(project: Project) = viewModelScope.launch {
        repo.addProject(project)
    }

    suspend fun deleteProject(id:Long){
        repo.deleteProject(id)
    }

    fun deleteAllProjects() = viewModelScope.launch {
//        projectRepository.deleteAllProjects()
//        getProjects()
    }

    fun viewForm() {
        showForm.postValue(!showForm.value!!)
    }

    fun createProject() {
        val title: String = formProjectTitle.value!!
        val description: String = formProjectDescription.value!!

        formProjectTitleError.postValue(false)
        formProjectDescriptionError.postValue(false)

        when {
            title.isBlank() -> {
                formProjectTitleError.postValue(true)
            }
            description.isBlank() -> {
                formProjectDescriptionError.postValue(true)
            }
            else -> {
                formProjectTitle.postValue("")
                formProjectDescription.postValue("")
                addProject(
                    Project(
                        id = (0..100L).random(),
                        title = title,
                        description = description,
                        uid = repo.getLoggedUser()
                    )
                )
                viewForm()
            }
        }
    }

}