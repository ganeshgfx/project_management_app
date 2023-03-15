package com.ganeshgfx.projectmanagement.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.GsonBuilder

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Project::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("projectId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val projectId: Long,
    val title: String,
    val description: String,
    var status: Status
) {
    override fun toString(): String = GsonBuilder()
        .setPrettyPrinting()
        .create()
        .toJson(this)

}

enum class Status {
    PENDING,
    IN_PROGRESS,
    DONE
}