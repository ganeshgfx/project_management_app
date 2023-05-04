package com.ganeshgfx.projectmanagement.models.gpt

import com.google.gson.annotations.SerializedName

data class GptRequest(
    @SerializedName("model")
    val model: String,
    @SerializedName("prompt")
    val prompt: String,
    @SerializedName("temperature")
    val temperature: Double,
    @SerializedName("max_tokens")
    val max_tokens: Int,
    @SerializedName("top_p")
    val top_p: Double,
    @SerializedName("frequency_penalty")
    val frequency_penalty: Double,
    @SerializedName("presence_penalty")
    val presence_penalty: Double
)
