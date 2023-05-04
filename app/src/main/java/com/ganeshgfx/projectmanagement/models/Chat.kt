package com.ganeshgfx.projectmanagement.models

data class Chat(
    val message: String,
    val isMe: Boolean,
    val time: Long = System.currentTimeMillis()
)
