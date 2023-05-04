package com.ganeshgfx.projectmanagement.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.adapters.ChatListAdapter
import com.ganeshgfx.projectmanagement.models.Chat
import com.ganeshgfx.projectmanagement.repositories.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repo: ChatRepository
) : ViewModel() {

    val chatsAdapter = ChatListAdapter()

    init {
        chatsAdapter.setData(listOf(Chat("Hello", true), Chat("Hello?", false)))
    }

    val msg = MutableLiveData("")

    fun sendMsg() = viewModelScope.launch {

    }

}