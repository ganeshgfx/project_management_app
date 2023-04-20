package com.ganeshgfx.projectmanagement.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.GsonBuilder
import java.util.*
import com.ganeshgfx.projectmanagement.Utils.toDate

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
    @PrimaryKey
    val id: String = "",
    val projectId: String = "",
    val title: String = "",
    val description: String = "",
    var status: Status = Status.PENDING,
    var startDate: Long? = null,
    var endDate: Long? = null,
    var assignedTo: String = ""
) {
    override fun toString(): String = GsonBuilder()
        .setPrettyPrinting()
        .create()
        .toJson(this)

    fun toDate(): String = toDate(startDate,endDate)
}

enum class Status {
    PENDING,
    IN_PROGRESS,
    DONE
}