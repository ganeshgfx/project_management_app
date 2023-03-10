package com.ganeshgfx.projectmanagement.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ganeshgfx.projectmanagement.database.ProjectDatabase
import com.ganeshgfx.projectmanagement.models.Project

class ProjectRepository(
    private val projectDatabase: ProjectDatabase
) {

    val projects = MutableLiveData<List<Project>>()

    suspend fun getAllProjects() : List<Project> {
        val allProjects = projectDatabase.projectDao().getAllProjects()
        //Log.d("TAG", "getAllProjects: ${allProjects}")
        projects.postValue(allProjects)
        return allProjects
    }

    suspend fun addProject(project: Project){
        projectDatabase.projectDao().insertProject(project)
    }

    suspend fun deleteAllProjects(){
        projectDatabase.projectDao().deleteAllProjects()
    }
}