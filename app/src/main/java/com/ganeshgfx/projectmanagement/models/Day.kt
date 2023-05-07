package com.ganeshgfx.projectmanagement.models


import com.ganeshgfx.projectmanagement.Utils.getMonthNameShort

data class Day(
    val day: Int,
    val month: Int,
    val year: Int,
    var isTaskDay: Boolean = false
) {
    val text: String get() = day.toString()
    val date get() = "$day/$month/$year"
    val monthText get() = getMonthNameShort(month)
}
