package com.ganeshgfx.projectmanagement.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganeshgfx.projectmanagement.models.Project
import com.ganeshgfx.projectmanagement.repositories.ProjectRepository
import kotlinx.coroutines.launch

class ProjectViewModel(private val projectRepository: ProjectRepository) : ViewModel() {

    init {
        getProjects()
    }

    val projects :LiveData<List<Project>> get() = projectRepository.projects

    val showForm = MutableLiveData(false)
    val formProjectTitle = MutableLiveData("")
    val formProjectTitleError = MutableLiveData(false)
    val formProjectDescription = MutableLiveData("")
    val formProjectDescriptionError = MutableLiveData(false)

    fun getProjects() = viewModelScope.launch {
        projectRepository.getAllProjects()
    }

    fun addProject(project: Project) = viewModelScope.launch {
        projectRepository.addProject(project)
        getProjects()
    }
    fun deleteAllProjects() = viewModelScope.launch{
//        projectRepository.deleteAllProjects()
//        getProjects()
    }
    fun viewForm(){
        showForm.postValue(!showForm.value!!)
    }
    fun createProject() {
        val title : String = formProjectTitle.value!!
        val description : String  = formProjectDescription.value!!

        formProjectTitleError.postValue(false)
        formProjectDescriptionError.postValue(false)

        when{
            title.isBlank()-> {
                formProjectTitleError.postValue(true)
            }
            description.isBlank()-> {
                formProjectDescriptionError.postValue(true)
            }
            else ->{
                formProjectTitle.postValue("")
                formProjectDescription.postValue("")
                addProject(Project(id = (0..100L).random(), title = title, description = description))
                viewForm()
            }
        }
    }

}