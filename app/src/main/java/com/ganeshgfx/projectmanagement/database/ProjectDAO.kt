package com.ganeshgfx.projectmanagement.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ganeshgfx.projectmanagement.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDAO {
    //project
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProject(project: Project)

    @Query("SELECT * FROM project WHERE id = :id")
    fun getProject(id:Long): Flow<Project>

    @Query("DELETE FROM project WHERE 1")
    suspend fun deleteAllProjects()

    @Query("SELECT status,COUNT(id) AS count  FROM  task WHERE projectId = :projectId  GROUP BY status;")
    fun tasksStatus(projectId: Long): Flow<List<ProjectTaskCount>>

    @Query("SELECT * FROM project")
    suspend fun getProjectWithTasks(): List<ProjectWithTasks>

    @Query("SELECT * FROM project ORDER BY title")
    fun getProjectWithTasksFlow(): Flow<List<ProjectWithTasks>>

    @Query("DELETE FROM project WHERE id = :id")
    suspend fun deleteProject(id: Long)

    //task
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTask(task: Task) : Long

    @Query("SELECT * FROM task WHERE projectId = :projectId")
    suspend fun getAllTasks(projectId: Long): List<Task>

    @Query("SELECT * FROM task WHERE projectId = :projectId AND status IN (:status)")//ORDER BY status DESC
    fun getTasksFlow(
        projectId: Long,
        status: List<Status>
    ): Flow<List<Task>>

    @Update
    suspend fun updateTask(task: Task): Int

}