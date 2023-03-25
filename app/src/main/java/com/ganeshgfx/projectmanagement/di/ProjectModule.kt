package com.ganeshgfx.projectmanagement.di

import com.ganeshgfx.projectmanagement.database.FirestoreHelper
import com.ganeshgfx.projectmanagement.database.ProjectDAO
import com.ganeshgfx.projectmanagement.database.ProjectDatabase
import com.ganeshgfx.projectmanagement.repositories.ProjectRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProjectModule {
    @Singleton
    @Provides
    fun provideProjectDAO(db: ProjectDatabase): ProjectDAO {
        return db.projectDao()
    }

//    @Singleton
//    @Provides
//    fun provideProjectRepo(dao: ProjectDAO,remote: FirebaseFirestore) = ProjectRepository(dao,remote)

    @Singleton
    @Provides
    fun provideProjectRepo(dao: ProjectDAO,helper: FirestoreHelper): ProjectRepository {
        return ProjectRepository(dao,helper)
    }

}