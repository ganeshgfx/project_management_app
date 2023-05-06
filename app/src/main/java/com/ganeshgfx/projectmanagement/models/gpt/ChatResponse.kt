package com.ganeshgfx.projectmanagement.models.gpt

import com.google.gson.annotations.SerializedName

data class ChatResponse(
    @SerializedName("id") var id: String,
    @SerializedName("created") var created: Long,
    @SerializedName("model") var model: String,
    @SerializedName("usage") var usage: Usage,
    @SerializedName("choices") var choices: List<Choices>
)