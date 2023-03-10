package com.ganeshgfx.projectmanagement.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MainActivityViewModel(app: Application) : AndroidViewModel(app) {

    private var _isProjectFragmentsActive = MutableLiveData(true)
    val isProjectFragmentsActive : LiveData<Boolean>
        get() = _isProjectFragmentsActive

    fun hideBottomAppBar(isProjectFragmentsActive:Boolean){
        _isProjectFragmentsActive.postValue(isProjectFragmentsActive)
    }
}