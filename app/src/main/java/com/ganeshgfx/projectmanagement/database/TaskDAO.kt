package com.ganeshgfx.projectmanagement.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ganeshgfx.projectmanagement.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTask(task: Task) : Long

    @Query("SELECT * FROM task WHERE projectId = :projectId AND status IN (:status)")//ORDER BY status DESC
    fun getTasksFlow(
        projectId: String,
        status: List<Status>
    ): Flow<List<Task>>

    @Update
    suspend fun updateTask(task: Task): Int

}