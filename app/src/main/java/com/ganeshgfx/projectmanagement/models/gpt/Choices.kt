package com.ganeshgfx.projectmanagement.models.gpt

import com.ganeshgfx.projectmanagement.models.gpt.Message
import com.google.gson.annotations.SerializedName

data class Choices(
    @SerializedName("message") var message: Message,
    @SerializedName("finish_reason") var finishReason: String,
    @SerializedName("index") var index: Int,
)