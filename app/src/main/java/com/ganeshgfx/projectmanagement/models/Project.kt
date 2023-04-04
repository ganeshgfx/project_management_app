package com.ganeshgfx.projectmanagement.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.ganeshgfx.projectmanagement.Utils.log

@Entity
data class Project(
    @PrimaryKey
    val id: String = "",
    val uid: String = "",
    val title: String = "",
    val description: String = "",
)

data class ProjectTaskCount(
    val status: Status,
    val count: Int
)

data class ProjectWithTasks(
    @Embedded
    val project: Project,
    @Relation(parentColumn = "id", entityColumn = "projectId")
    val tasks: List<Task>
) {
    fun getStatusCount(status: Status): Int {
        val result = tasks.filter { it.status == status }
        return result.size
    }
}