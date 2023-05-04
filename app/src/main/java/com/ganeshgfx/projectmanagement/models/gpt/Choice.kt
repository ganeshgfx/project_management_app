package com.ganeshgfx.projectmanagement.models.gpt

import com.google.gson.annotations.SerializedName

data class Choice(
    @SerializedName("text")
    val text: String
)
