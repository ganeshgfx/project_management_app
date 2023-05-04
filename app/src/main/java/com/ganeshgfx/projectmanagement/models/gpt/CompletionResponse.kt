package com.ganeshgfx.projectmanagement.models.gpt

import com.google.gson.annotations.SerializedName

data class CompletionResponse(
    @SerializedName("choices")
    val choices: List<Choice>
    )