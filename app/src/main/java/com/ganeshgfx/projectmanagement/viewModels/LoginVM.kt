package com.ganeshgfx.projectmanagement.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.models.User
import com.ganeshgfx.projectmanagement.repositories.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor(private val repo: AuthRepo) : ViewModel() {

    init {
        repo.isLogged.onEach {
            if (it!=null) {
                var photo = ""
                if(it.photoUrl!=null){
                    photo = it.photoUrl.toString()
                }
                val user = User(uid = it.uid, displayName = it.displayName!!, profile = photo)
                val result = repo.addLoggedUser(user)
                log(user,result)
                isLogged.postValue(result)
            }
        }.launchIn(viewModelScope)
    }

    val isLogged = MutableLiveData(false)
}