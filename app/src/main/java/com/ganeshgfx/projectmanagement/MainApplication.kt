package com.ganeshgfx.projectmanagement

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.widget.Toast
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.services.DataService
import com.ganeshgfx.projectmanagement.services.NotificationHelper
import com.google.android.material.color.DynamicColors.applyToActivitiesIfAvailable
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        applyToActivitiesIfAvailable(this)
        createNotificationChannel()
        startAppService()
    }

    fun startAppService() {
        //val intent = Intent(this, MainServices::class.java)
        //startService(intent)
        //startForegroundService(intent)
        startJob()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NotificationHelper.GENERAL,
            "General",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "General Notifications"

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun startJob(){
        val component = ComponentName(this,DataService::class.java)
        val info = JobInfo.Builder(123,component)
            .setPersisted(true)
            .build()
        val scheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        val result = scheduler.schedule(info)
        if(result==JobScheduler.RESULT_SUCCESS){
            log("JOB SCHEDULED")
        }else{
            log("JOB SCHEDULE FAILED")
        }
    }
    private fun stopJob(){
        val scheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler.cancel(123)
        log("JOB STOPPED")
    }

    open fun copyText(textToCopy: String):String {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", textToCopy)
        clipboardManager.setPrimaryClip(clipData)

       if (checkVersionLessThanR()) {
            Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        return clipboardManager.primaryClip?.getItemAt(0)?.text.toString()
    }

    fun checkVersionLessThanR(): Boolean {
        return android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.R
    }

}