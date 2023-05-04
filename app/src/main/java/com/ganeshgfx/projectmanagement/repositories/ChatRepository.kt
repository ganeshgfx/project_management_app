package com.ganeshgfx.projectmanagement.repositories

import com.ganeshgfx.projectmanagement.Utils.randomString
import com.ganeshgfx.projectmanagement.models.Chat
import kotlinx.coroutines.delay

class ChatRepository {
    private var _isBusy = false
    val isBusy get() = _isBusy
    suspend fun chat(msg: String): Chat {
        _isBusy = true
        delay(999)
        _isBusy = false
        return Chat(randomString(9), false)
    }
}