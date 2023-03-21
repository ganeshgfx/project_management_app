package com.ganeshgfx.projectmanagement.di

import android.content.Context
import com.ganeshgfx.projectmanagement.database.FirebaseHelper
import com.ganeshgfx.projectmanagement.database.ProjectDAO
import com.ganeshgfx.projectmanagement.database.ProjectDatabase
import com.ganeshgfx.projectmanagement.repositories.ManageProjectRepo
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFirebaseHelper():FirebaseHelper{
        return FirebaseHelper(FirebaseAuth.getInstance())
    }

    @Singleton
    @Provides
    fun provideDB(@ApplicationContext context:Context): ProjectDatabase {
        return ProjectDatabase.getDatabase(context)
    }

}