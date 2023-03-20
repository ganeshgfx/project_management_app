package com.ganeshgfx.projectmanagement.Utils

import android.util.Log

fun log(vararg log: Any, TAG: String = "project_app") {
    var temp = ""
    if (log.size>1) for (log_ in log)
        temp+="$log_, "
    else temp = log[0].toString()
    Log.d(TAG, temp)
}