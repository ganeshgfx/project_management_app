package com.ganeshgfx.projectmanagement.di

import android.content.Context
import com.ganeshgfx.projectmanagement.database.*
import com.ganeshgfx.projectmanagement.repositories.MainActivityRepository
import com.ganeshgfx.projectmanagement.services.Notifications
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDB(@ApplicationContext context: Context) = ProjectDatabase.getDatabase(context)

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob())

    @Provides
    @Singleton
    fun provideNotification(@ApplicationContext context: Context): Notifications = Notifications(context)

    @Provides
    @Singleton
    fun provideMainActivityRepository(
        projectDAO: ProjectDAO,
        userDAO: UserDAO,
        taskDAO: TaskDAO,
        remote: FirestoreHelper,
        scope: CoroutineScope
    ): MainActivityRepository = MainActivityRepository(userDAO, projectDAO, taskDAO, remote, scope)

}