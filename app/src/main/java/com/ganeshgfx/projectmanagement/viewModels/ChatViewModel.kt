package com.ganeshgfx.projectmanagement.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganeshgfx.projectmanagement.Utils.log
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

    var currentProjectId = ""

    val chatsAdapter = ChatListAdapter()

    val msg = MutableLiveData("")

    fun sendMsg() = viewModelScope.launch {
        val sendText = msg.value!!
        if (sendText.isNotBlank() && !repo.isBusy) {
            val chat = repo.chat(sendText,currentProjectId)
            chat?.let {
                msg.postValue("")
                val chats = listOf(Chat(sendText, true), it)
                chatsAdapter.addData(chats)
            }
        }
    }

}