package com.ganeshgfx.projectmanagement.di

import com.ganeshgfx.projectmanagement.database.ProjectDAO
import com.ganeshgfx.projectmanagement.repositories.TaskListRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TaskModule {

    @Singleton
    @Provides
    fun provideTaskRepo(dao: ProjectDAO): TaskListRepository {
        return TaskListRepository(dao)
    }

}