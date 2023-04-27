package com.ganeshgfx.projectmanagement

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ganeshgfx.projectmanagement.services.MainServices

class AutoRun : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
//val intent = Intent(context, MainServices::class.java)
//.startForegroundService(intent)
//.startService(intent)
        //val app = context.applicationContext as MainApplication
       //app.startAppService()
    }
}