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
    fun tasksStatus(projectId: String): Flow<List<ProjectTaskCount>>

    @Query("SELECT * FROM project ORDER BY title")
    fun getProjectWithTasksFlow(): Flow<List<ProjectWithTasks>>

    @Query("DELETE FROM project WHERE id = :id")
    suspend fun deleteProject(id: Long)


}