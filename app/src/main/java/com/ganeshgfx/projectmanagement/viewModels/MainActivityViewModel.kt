package com.ganeshgfx.projectmanagement.viewModels

import androidx.lifecycle.*
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.repositories.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(authRepo: AuthRepo) : ViewModel() {

    init {
        authRepo.isLogged.onEach {
            _isLogged.postValue(it)
        }.launchIn(viewModelScope)
//        authRepo.getUsers().onEach {
//            log(it)
//        }.launchIn(viewModelScope)
    }

    private val _isLogged = MutableLiveData(true)
    val isLogged:LiveData<Boolean> get() = _isLogged

    private var _isProjectFragmentsActive = MutableLiveData(true)

    private var _currentProjectId = -1L
    val currentProjectId : Long get() = _currentProjectId

    val isProjectFragmentsActive : LiveData<Boolean>
        get() = _isProjectFragmentsActive

    fun hideBottomAppBar(isProjectFragmentsActive:Boolean){
        _isProjectFragmentsActive.postValue(isProjectFragmentsActive)
    }

    fun changeProject(projectId:Long){
        _currentProjectId = projectId
    }
}