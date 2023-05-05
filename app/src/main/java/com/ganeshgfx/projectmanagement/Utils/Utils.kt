package com.ganeshgfx.projectmanagement.Utils

import android.annotation.SuppressLint
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.ganeshgfx.projectmanagement.models.Day
import com.ganeshgfx.projectmanagement.services.MainServices
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import java.time.YearMonth
import java.util.*

fun randomString(length: Int): String {
    var str = ""
    val set = mutableSetOf<Char>()
    set.addAll('0'..'9')
    set.addAll('a'..'z')
    set.addAll('A'..'Z')
    for (i in 0..length) {
        str += set.random()
    }
    return str
}

fun toDate(start: Long?, end: Long?): String =
    "${
        if (start != null) {
            dateString(start) + " - "
        } else ""
    } ${
        if (end != null) {
            dateString(end)
        } else ""
    }"

private const val DD_MM_YYYY = "dd/MM/yyyy"

fun dateString(start: Long): String? =
    SimpleDateFormat(DD_MM_YYYY).format(Date(start))

fun epochMillis(date: String): Long {
    val format = SimpleDateFormat(DD_MM_YYYY)
    val date = format.parse(date)
    val calendar = Calendar.getInstance()
    calendar.time = date
    val epochMillis: Long = calendar.timeInMillis
    return epochMillis
}

fun dateStringToDay(dateString: String):Day{
    val format = SimpleDateFormat(DD_MM_YYYY)
    val date = format.parse(dateString)
    val calendar = Calendar.getInstance()
    calendar.time = date
    return Day(
        calendar.get(Calendar.DAY_OF_MONTH),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.YEAR)
    )
}


fun isInRange(dateString: String, startDateString: String, endDateString: String): Boolean {
    val dateFormat = SimpleDateFormat(DD_MM_YYYY, Locale.getDefault())
    val date = dateFormat.parse(dateString)
    val startDate = dateFormat.parse(startDateString)
    val endDate = dateFormat.parse(endDateString)
    return date in startDate..endDate
}

fun hideSoftKeyBord(view: View) {
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun randomColor(alpha: Int = 100): Int {
    return Color.argb(
        alpha,
        (0..256).random(),
        (0..256).random(),
        (0..256).random()
    )
}

fun makeShape(topRight: Float, topLeft: Float, bottomRight: Float, bottomLeft: Float) =
    ShapeAppearanceModel()
        .toBuilder()
        .setTopLeftCorner(CornerFamily.ROUNDED, topLeft)
        .setTopRightCorner(CornerFamily.ROUNDED, topRight)
        .setBottomLeftCorner(CornerFamily.ROUNDED, bottomLeft)
        .setBottomRightCorner(CornerFamily.ROUNDED, bottomRight)
        .build()


fun getLastDay(year: Int, month: Int) =
    YearMonth.of(year, month).atEndOfMonth().dayOfMonth