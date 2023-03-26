package com.ganeshgfx.projectmanagement.models

import androidx.room.*

@Entity(
    primaryKeys = ["uid", "projectId"],
    foreignKeys = [
        ForeignKey(
            entity = Project::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("projectId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("uid"),
            childColumns = arrayOf("uid"),
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class Member(
    val projectId: String,
    val uid: String
)
data class ProjectMember(
    @Embedded
    val member: Member?=null,
    @Relation(parentColumn = "uid", entityColumn = "uid")
    val user: User

)