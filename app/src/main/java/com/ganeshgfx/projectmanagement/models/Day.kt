package com.ganeshgfx.projectmanagement.models

data class Day(
    val day: Int,
    val month: Int,
    val year: Int,
    var isTaskDay: Boolean = false
) {
    val text: String get() = day.toString()
    val date get() = "$day/$month/$year"
}
