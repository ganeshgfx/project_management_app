package com.ganeshgfx.projectmanagement

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.services.DataService
import com.ganeshgfx.projectmanagement.services.MainServices
import com.ganeshgfx.projectmanagement.services.Notifications
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
            Notifications.GENERAL,
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
}