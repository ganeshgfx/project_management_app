package com.ganeshgfx.projectmanagement.models.gpt

data class Information(
    val projectInfo : List<String>,
    val currently_logged_user : String,
    val userList: List<String>,
    val taskCount : Int,
    //TODO Group pending done doing
    val taskList : List<String>,
)
