package com.ganeshgfx.projectmanagement.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganeshgfx.projectmanagement.adapters.ChatListAdapter
import com.ganeshgfx.projectmanagement.models.Chat
import com.ganeshgfx.projectmanagement.repositories.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repo: ChatRepository
) : ViewModel() {

    private var _currentProjectId = ""
    val currentProjectId get() = _currentProjectId

    fun setCurrentProjectId(id: String) = viewModelScope.launch {
        _receiving.postValue(true)
        _currentProjectId = id
        val msg = "greet me and give list of questions that can i ask regarding this project"
        val chat = repo.chat(msg, currentProjectId)
        chat?.let {
            val chats = listOf(it)
            chatsAdapter.addData(chats)
        }
        _receiving.postValue(false)
    }

    private val _receiving = MutableLiveData(false)
    val receiving: LiveData<Boolean> get() = _receiving

    val chatsAdapter = ChatListAdapter()

    val msg = MutableLiveData("")

    fun sendMsg() = viewModelScope.launch {
        val sendText = msg.value!!
        if (sendText.isNotBlank() && !repo.isBusy) {
            _receiving.postValue(true)
            msg.postValue("")
            val chat = repo.chat(sendText, currentProjectId)
            chatsAdapter.addData(listOf(Chat(sendText, true)))
            chat?.let {
                val chats = listOf(it)
                chatsAdapter.addData(chats)
            }
            _receiving.postValue(false)
        }
    }

}