package com.ganeshgfx.projectmanagement.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Project(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val title: String,
    val description: String,
)

data class ProjectTaskCount(
    val status: Status,
    val count: Int
)
