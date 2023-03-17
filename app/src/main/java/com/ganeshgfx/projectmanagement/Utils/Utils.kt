package com.ganeshgfx.projectmanagement.Utils

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.gson.Gson
import java.util.*

fun randomString(length:Int):String{
    var str = ""
    val set = mutableSetOf<Char>()
    set.addAll('0'..'9')
    set.addAll('a'..'z')
    set.addAll('A'..'Z')
    for (i in 0..length){
        str+=set.random()
    }
    return str
}
fun toDate(date:Long?): String =
    if (date != null) {
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy")
        val date = dateFormatter.format(Date(date))
        date
    } else {
        ""
    }
fun hideSoftKeyBord(view: View) {
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}