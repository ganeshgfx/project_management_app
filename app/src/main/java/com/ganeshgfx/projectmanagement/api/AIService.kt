package com.ganeshgfx.projectmanagement.api

import com.ganeshgfx.projectmanagement.models.gpt.ChatRequest
import com.ganeshgfx.projectmanagement.models.gpt.ChatResponse
import com.ganeshgfx.projectmanagement.models.gpt.CompletionResponse
import com.ganeshgfx.projectmanagement.models.gpt.GptRequest
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


