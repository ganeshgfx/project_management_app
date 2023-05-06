package com.ganeshgfx.projectmanagement.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ganeshgfx.projectmanagement.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTask(task: Task) : Long

    @Query("SELECT * FROM task WHERE projectId = :projectId AND status IN (:status) ORDER BY status ASC")
    fun getTasksFlow(
        projectId: String,
        status: List<Status>
    ): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE projectId = :projectId")
    suspend fun getTasks(
        projectId: String
    ): List<Task>

    @Query("SELECT * FROM task WHERE projectId = :projectId AND id = :taskId")
    suspend fun getTask(
        projectId: String,
        taskId: String
    ): Task

    @Update
    suspend fun updateTask(task: Task): Int

    @Delete
    suspend fun deleteTask(task: Task): Int

}