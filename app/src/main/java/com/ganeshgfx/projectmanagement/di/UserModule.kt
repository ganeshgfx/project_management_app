package com.ganeshgfx.projectmanagement.di

import com.ganeshgfx.projectmanagement.database.FirestoreHelper
import com.ganeshgfx.projectmanagement.database.ProjectDatabase
import com.ganeshgfx.projectmanagement.database.UserDAO
import com.ganeshgfx.projectmanagement.repositories.UserRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserModule {
    @Singleton
    @Provides
    fun provideUserDao(db: ProjectDatabase) = db.userDao()

    @Singleton
    @Provides
    fun provideUserRepo(dao: UserDAO,helper: FirestoreHelper, scope: CoroutineScope) = UserRepo(dao,helper,scope)
}