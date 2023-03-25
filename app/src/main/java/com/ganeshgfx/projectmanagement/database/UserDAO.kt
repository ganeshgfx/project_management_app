package com.ganeshgfx.projectmanagement.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Upsert
import com.ganeshgfx.projectmanagement.models.Task
import com.ganeshgfx.projectmanagement.models.User

@Dao
interface UserDAO {
    @Upsert
    suspend fun insertUser(user: User): Long
}