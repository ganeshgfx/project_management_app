package com.ganeshgfx.projectmanagement.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.GsonBuilder

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id : Long,
    val title : String,
    val description : String,
    val status: Status
){
    override fun toString(): String = GsonBuilder()
            .setPrettyPrinting()
            .create()
            .toJson(this)

}
enum class Status{
    PENDING,
    IN_PROGRESS,
    DONE
}