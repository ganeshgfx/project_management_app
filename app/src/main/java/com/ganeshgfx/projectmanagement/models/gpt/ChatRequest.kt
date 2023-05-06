package com.ganeshgfx.projectmanagement.models.gpt

import com.ganeshgfx.projectmanagement.models.gpt.Message
import com.google.gson.annotations.SerializedName

data class ChatRequest(
    @SerializedName("model") val model: String,
    @SerializedName("messages") val messages: List<Message>,
    @SerializedName("presence_penalty") val presence_penalty: Double,
    @SerializedName("stream") val stream: Boolean,
    @SerializedName("temperature") val temperature: Double
)