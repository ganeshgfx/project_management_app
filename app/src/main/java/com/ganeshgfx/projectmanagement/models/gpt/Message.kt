package com.ganeshgfx.projectmanagement.models.gpt

import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("role") var role: String,
    @SerializedName("content") var content: String,
)