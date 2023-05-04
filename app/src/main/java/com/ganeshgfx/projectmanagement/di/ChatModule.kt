package com.ganeshgfx.projectmanagement.di

import com.ganeshgfx.projectmanagement.api.AIService
import com.ganeshgfx.projectmanagement.api.RetrofitHelper
import com.ganeshgfx.projectmanagement.database.ProjectDAO
import com.ganeshgfx.projectmanagement.database.TaskDAO
import com.ganeshgfx.projectmanagement.database.UserDAO
import com.ganeshgfx.projectmanagement.repositories.ChatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Provides
    @Singleton
    fun provideAIService() = RetrofitHelper.gptApiService

    @Provides
    @Singleton
    fun provideChatRepo(
        service: AIService,
        usersDAO: UserDAO,
        projectDAO: ProjectDAO,
        taskDAO: TaskDAO,
    )
    : ChatRepository = ChatRepository(service,usersDAO,projectDAO,taskDAO)

}