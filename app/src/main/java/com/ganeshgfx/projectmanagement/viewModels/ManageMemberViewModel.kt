package com.ganeshgfx.projectmanagement.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.adapters.UserListAdapter
import com.ganeshgfx.projectmanagement.repositories.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageMemberViewModel @Inject constructor(
    private val repo: UserRepo
) : ViewModel() {
    val search = MutableLiveData("")
    val userListAdapter = UserListAdapter()
    fun search() = viewModelScope.launch {
        val search: String = search.value!!.trim()
        if (search.isNotBlank()) {
            val res = repo.searchUser(search)
            //log(res)
            userListAdapter.setData(res)
        }
    }
}