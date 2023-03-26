package com.ganeshgfx.projectmanagement.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.GsonBuilder
import java.util.*

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Project::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("projectId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val projectId: String,
    val title: String,
    val description: String,
    var status: Status,
    var dueDate: Long? = null
) {
    override fun toString(): String = GsonBuilder()
        .setPrettyPrinting()
        .create()
        .toJson(this)

    fun toDate(): String = com.ganeshgfx.projectmanagement.Utils.toDate(dueDate)
}

enum class Status {
    PENDING,
    IN_PROGRESS,
    DONE
}