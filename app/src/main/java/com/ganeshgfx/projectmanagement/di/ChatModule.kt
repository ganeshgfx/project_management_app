package com.ganeshgfx.projectmanagement.di

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
    fun provideChatRepo(): ChatRepository = ChatRepository()
}