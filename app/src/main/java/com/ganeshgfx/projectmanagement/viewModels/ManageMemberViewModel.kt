package com.ganeshgfx.projectmanagement.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.adapters.UserListAdapter
import com.ganeshgfx.projectmanagement.adapters.UserListAdapter.*
import com.ganeshgfx.projectmanagement.models.ProjectMember
import com.ganeshgfx.projectmanagement.models.User
import com.ganeshgfx.projectmanagement.repositories.ProjectRepository
import com.ganeshgfx.projectmanagement.repositories.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageMemberViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val repo: ProjectRepository
) : ViewModel() {
    val search:MutableLiveData<String> = MutableLiveData("")
    val userListAdapter = UserListAdapter()
    private var _currentProjectId = ""
    private var getProjectMembersJob: Job? = null
    val loading = MutableLiveData(false)
    val searchView = MutableLiveData(false)
    init {
        //adding user from search
        userListAdapter.setOnClickListener { user, event ->
            viewModelScope.launch {
                loading.postValue(true)
                if (event == Event.ADD) {
                    userRepo.addMember(user, _currentProjectId)
                    search.postValue("")
                    "".isNotBlank()
                }
                if (event == Event.DELETE) {
                    userRepo.deleteProjectMember(_currentProjectId, user.uid)
                }
                loading.postValue(false)
            }
        }
    }

    fun setProjectId(id: String) {
        _currentProjectId = id
        loadUsers()
    }

    private fun loadUsers() {
        getProjectMembersJob?.cancel()
        getProjectMembersJob = viewModelScope.launch {
            userRepo.getProjectMembers(_currentProjectId).collect{
                //log(it)
                userListAdapter.setData(it)
            }
        }
    }

    fun search() = viewModelScope.launch {
        val search: String = search.value!!.trim()
        if (search.isNotBlank()) {
            loading.postValue(true)
            val res = userRepo.searchUser(search).map { ProjectMember(user = it) }
            userListAdapter.setSearchData(res)
            loading.postValue(false)
        }
    }
}