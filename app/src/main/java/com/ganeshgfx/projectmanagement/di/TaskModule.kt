package com.ganeshgfx.projectmanagement.di

import com.ganeshgfx.projectmanagement.database.ProjectDAO
import com.ganeshgfx.projectmanagement.database.ProjectDatabase
import com.ganeshgfx.projectmanagement.database.TaskDAO
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
    fun provideTaskDao(db: ProjectDatabase) = db.taskDao()

    @Singleton
    @Provides
    fun provideTaskRepo(dao: TaskDAO): TaskListRepository {
        return TaskListRepository(dao)
    }

}