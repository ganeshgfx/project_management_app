package com.ganeshgfx.projectmanagement.Utils

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder

fun log(vararg log: Any, TAG: String = "project_app") {
    var temp = ""
    if (log.size>1) {
//        for (log_ in log) {
//            temp += "$log_\n, "
//        }
        temp = log.map { "$it" }.toString()
    } else {
        temp = log[0].toString()
    }
    Log.d(TAG, temp)
}