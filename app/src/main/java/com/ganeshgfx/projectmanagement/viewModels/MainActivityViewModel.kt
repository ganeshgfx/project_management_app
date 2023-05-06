package com.ganeshgfx.projectmanagement.viewModels

import androidx.lifecycle.*
import com.ganeshgfx.projectmanagement.repositories.AuthRepo
import com.ganeshgfx.projectmanagement.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    authRepo: AuthRepo,
    private val repo: MainRepository
) : ViewModel() {

    init {
        authRepo.isLogged.onEach {
          if(it!=null){
              _isLogged.postValue(true)
          }else{
              _isLogged.postValue(false)
          }
        }.launchIn(viewModelScope)

    }

    fun startRepo(){
        repo.startJob()
    }

    val notification = repo.notification

    private val _isLogged = MutableLiveData(true)
    val isLogged:LiveData<Boolean> get() = _isLogged

    private var _isProjectFragmentsActive = MutableLiveData(true)

    private var _currentProjectId = ""
    val currentProjectId : String get() = _currentProjectId

    val isProjectFragmentsActive : LiveData<Boolean>
        get() = _isProjectFragmentsActive

    fun hideBottomAppBar(isProjectFragmentsActive:Boolean){
        _isProjectFragmentsActive.postValue(isProjectFragmentsActive)
    }

    fun changeProject(projectId:String){
        _currentProjectId = projectId
    }
}