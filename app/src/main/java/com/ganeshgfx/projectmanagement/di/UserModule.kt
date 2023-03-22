package com.ganeshgfx.projectmanagement.di

import com.ganeshgfx.projectmanagement.database.ProjectDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserModule {
    @Singleton
    @Provides
    fun provideUserDao(db: ProjectDatabase) = db.userDao()
}