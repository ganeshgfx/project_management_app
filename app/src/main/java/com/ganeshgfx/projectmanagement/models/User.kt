package com.ganeshgfx.projectmanagement.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey
    val uid: String = "",
    val displayName:String? = ""
)