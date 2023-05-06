package com.ganeshgfx.projectmanagement.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.adapters.ChatListAdapter
import com.ganeshgfx.projectmanagement.models.Chat
import com.ganeshgfx.projectmanagement.repositories.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repo: ChatRepository
) : ViewModel() {

    private var _currentProjectId = ""
    val currentProjectId get() = _currentProjectId


    private var greeted = false

    fun setCurrentProjectId(id: String, help: String) = viewModelScope.launch {
        _receiving.postValue(true)
        _currentProjectId = id
        if (!greeted) {
            greeted = true

            val msg = if(help.isNotBlank()) help else  "greet me and give list of questions that can i ask regarding this project"


            var chat: Chat? = Chat("Error", false)
            try {
               // chat = repo.chat(msg, currentProjectId)
            } catch (e: Exception) {
                Log.e("project_app", "sendMsg: $e", e)
            }
            chat?.let {
                val chats = listOf(it)
                chatsAdapter.addData(chats)
            }
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
            var chat = Chat("Error", false)
            try {
                chat = repo.chat(sendText, currentProjectId)!!
            } catch (e: Exception) {
                Log.e("project_app", "sendMsg: $e", e)
            }
            chatsAdapter.addData(listOf(Chat(sendText, true)))
            chat?.let {
                val chats = listOf(it)
                chatsAdapter.addData(chats)
            }
            _receiving.postValue(false)
        }
    }

}