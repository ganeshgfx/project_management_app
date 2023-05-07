package com.ganeshgfx.projectmanagement.api

import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.models.gpt.ChatRequest
import com.ganeshgfx.projectmanagement.models.gpt.Message
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object OpenAiHelper {
    private const val BASE_URL = "https://api.openai.com/v1/"

    var interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    var client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

    var gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val api: OpenAi = retrofit.create(OpenAi::class.java)

    suspend fun requestChat(messages: List<Message>): List<Message> {

        val request = ChatRequest(
            model = "gpt-3.5-turbo",
            messages = messages,
            presence_penalty = 0.6,
            stream = false,
            temperature = 0.9
        )
        val response = api.getChatCompletion(request)
        if (response.isSuccessful) {

            return response.body()?.choices?.map {
                it.message
            }!!
        } else {
            val responseBody = response.errorBody()
            val responseText = responseBody?.let { String(it.bytes(), Charsets.UTF_8) }
            log("RETROFIT_ERROR", response.code().toString(), responseText.toString())
            return emptyList()
        }
    }
}
