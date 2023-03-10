package com.ganeshgfx.projectmanagement.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Project(
    @PrimaryKey(autoGenerate = false)
    val id : Long,
    val title : String,
    val description : String,
)
