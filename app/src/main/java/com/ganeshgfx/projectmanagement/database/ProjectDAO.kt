package com.ganeshgfx.projectmanagement.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ganeshgfx.projectmanagement.models.Project
import com.ganeshgfx.projectmanagement.models.Task

@Dao
interface ProjectDAO {
    //project
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProject(project: Project)
    @Query("SELECT * FROM project")
    suspend fun getAllProjects(): List<Project>
    @Query("DELETE FROM project WHERE 1")
    suspend fun deleteAllProjects()

    //task
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTask(project: Project)
    @Query("SELECT * FROM task")
    fun getAllTasks():LiveData<List<Task>>

}