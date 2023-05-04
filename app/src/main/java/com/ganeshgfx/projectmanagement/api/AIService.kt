package com.ganeshgfx.projectmanagement.api

import com.ganeshgfx.projectmanagement.models.gpt.CompletionResponse
import com.ganeshgfx.projectmanagement.models.gpt.GptRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AIService {
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer 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"
    )
    @POST("completions")
    suspend fun getCompletion(@Body request: GptRequest): Response<CompletionResponse>
}