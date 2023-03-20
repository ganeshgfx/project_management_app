package com.ganeshgfx.projectmanagement.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class Project(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val uid:String,
    val title: String,
    val description: String,
)

data class ProjectTaskCount(
    val status: Status,
    val count: Int
)
data class ProjectWithTasks(
    @Embedded
    val project:Project,
    @Relation(parentColumn = "id", entityColumn = "projectId")
    val tasks : List<Task>
){
    fun getStatusCount(status: Status):Int{
        val pending = tasks.filter { it.status == status }
        return pending.size
    }
}