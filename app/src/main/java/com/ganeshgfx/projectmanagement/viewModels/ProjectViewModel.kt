package com.ganeshgfx.projectmanagement.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganeshgfx.projectmanagement.models.Project
import com.ganeshgfx.projectmanagement.models.ProjectWithTasks
import com.ganeshgfx.projectmanagement.repositories.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(private val repo: ProjectRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            repo.projectWithTasksFlow().collect {
                _projectWithTasksFlow.postValue(it)
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
    val addingProject: LiveData<Boolean> get() = repo.addingProject

    fun deleteAllProjects() = viewModelScope.launch {
//        projectRepository.deleteAllProjects()
//        getProjects()
    }

    fun viewForm() {
        showForm.postValue(!showForm.value!!)
    }

    fun createProject() = viewModelScope.launch {
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
                repo.addProject(title, description)
                viewForm()
            }
        }
    }

}