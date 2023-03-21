package com.ganeshgfx.projectmanagement.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.repositories.ManageProjectRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageProjectVM @Inject constructor(private val repo: ManageProjectRepo) : ViewModel() {
    init {
        log("in mp vm : ${repo.getUser()}")
        viewModelScope.launch {
            repo.getProject(82).collect {
                log(it)
            }
        }
    }
}