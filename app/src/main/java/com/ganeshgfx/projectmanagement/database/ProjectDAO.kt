package com.ganeshgfx.projectmanagement.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ganeshgfx.projectmanagement.models.Project
import com.ganeshgfx.projectmanagement.models.ProjectTaskCount
import com.ganeshgfx.projectmanagement.models.ProjectWithTasks
import com.ganeshgfx.projectmanagement.models.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDAO {
    //project
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProject(project: Project)
    @Query("SELECT * FROM project")
    suspend fun getAllProjects(): List<Project>
    @Query("DELETE FROM project WHERE 1")
    suspend fun deleteAllProjects()
    @Query("SELECT status,COUNT(id) AS count  FROM  task WHERE projectId = :projectId  GROUP BY status;")
    suspend fun getTasksCounts(projectId: Long): List<ProjectTaskCount>
    @Query("SELECT * FROM project")
    suspend fun getProjectWithTasks():List<ProjectWithTasks>
    @Query("SELECT * FROM project ORDER BY title")
    fun getProjectWithTasksFlow(): Flow<List<ProjectWithTasks>>
    @Query("DELETE FROM project WHERE id = :id")
    suspend fun deleteProject(id: Long)

    //task
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTask(task: Task)
    @Query("SELECT * FROM task WHERE projectId = :projectId")
    suspend fun getAllTasks(projectId:Long):List<Task>
    @Query("SELECT * FROM task WHERE projectId = :projectId")
    fun getTasksFlow(projectId:Long):Flow<List<Task>>
    @Update
    suspend fun updateTask(task: Task):Int

}