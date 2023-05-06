package com.ganeshgfx.projectmanagement.models.gpt

import com.google.gson.annotations.SerializedName

data class Usage(
    @SerializedName("prompt_tokens") var promptTokens: Int,
    @SerializedName("completion_tokens") var completionTokens: Int,
    @SerializedName("total_tokens") var totalTokens: Int,
)