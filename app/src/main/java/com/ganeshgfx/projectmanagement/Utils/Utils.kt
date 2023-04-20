package com.ganeshgfx.projectmanagement.Utils

import android.content.Context
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
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
            SimpleDateFormat("dd/MM/yyyy").format(Date(start)) + " - "
        } else ""
    } ${
        if (end != null) {
            SimpleDateFormat("dd/MM/yyyy").format(Date(end))
        } else ""
    }"

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