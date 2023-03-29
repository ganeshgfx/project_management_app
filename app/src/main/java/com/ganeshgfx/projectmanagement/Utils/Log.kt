package com.ganeshgfx.projectmanagement.Utils

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder

object GSON{
    val gson = GsonBuilder().setPrettyPrinting().create();
}

fun log(vararg log: Any, TAG: String = "project_app") {
    var temp = ""
    if (log.size>1) {
        for (log_ in log) {
            temp += "${GSON.gson.toJson(log_)}\n, "
        }
    } else {
        temp = log[0].toString()
    }
    Log.d(TAG, temp)
}