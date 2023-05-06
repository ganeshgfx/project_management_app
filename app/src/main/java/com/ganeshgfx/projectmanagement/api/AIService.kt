package com.ganeshgfx.projectmanagement.api

import com.ganeshgfx.projectmanagement.models.gpt.CompletionResponse
import com.ganeshgfx.projectmanagement.models.gpt.GptRequest
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AIService {
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer $KEY"
    )
    @POST("completions")
    suspend fun getCompletion(@Body request: GptRequest): Response<CompletionResponse>

    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer $KEY"
    )
    @POST("chat/completions")
    suspend fun getChatCompletion(@Body request: ChatRequest): Response<ChatResponse>
}

data class ChatRequest(
    @SerializedName("model") val model: String,
    @SerializedName("messages") val messages: List<Message>,
    @SerializedName("presence_penalty") val presence_penalty: Double,
    @SerializedName("stream") val stream: Boolean,
    @SerializedName("temperature") val temperature: Double
)


data class ChatResponse(
    @SerializedName("id") var id: String,
    @SerializedName("created") var created: Long,
    @SerializedName("model") var model: String,
    @SerializedName("usage") var usage: Usage,
    @SerializedName("choices") var choices: List<Choices>
)

data class Usage(
    @SerializedName("prompt_tokens") var promptTokens: Int,
    @SerializedName("completion_tokens") var completionTokens: Int,
    @SerializedName("total_tokens") var totalTokens: Int,
)

data class Choices(
    @SerializedName("message") var message: Message,
    @SerializedName("finish_reason") var finishReason: String,
    @SerializedName("index") var index: Int,
)

data class Message(
    @SerializedName("role") var role: String,
    @SerializedName("content") var content: String,
)