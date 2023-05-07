package com.ganeshgfx.projectmanagement.di

import com.ganeshgfx.projectmanagement.api.OpenAiHelper
import com.ganeshgfx.projectmanagement.database.FirestoreHelper
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
    fun provideAIService() = OpenAiHelper

    @Provides
    @Singleton
    fun provideChatRepo(
        service: OpenAiHelper,
        helper: FirestoreHelper,
        usersDAO: UserDAO,
        projectDAO: ProjectDAO,
        taskDAO: TaskDAO,
    )
    : ChatRepository = ChatRepository(service,helper,usersDAO,projectDAO,taskDAO)

}