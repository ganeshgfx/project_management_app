package com.ganeshgfx.projectmanagement.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganeshgfx.projectmanagement.repositories.TaskListRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TaskListViewModel(private val repository: TaskListRepository) : ViewModel() {
    val tasks get() = repository.tasks
    val showForm = MutableLiveData(false)

    init {

        repository.getTasks(6)

    }

    fun viewForm() {
        showForm.postValue(!showForm.value!!)
    }

    fun addTasks() {
        repository.getTasks(10)
        viewForm()
    }
}