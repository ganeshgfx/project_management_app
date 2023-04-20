package com.ganeshgfx.projectmanagement

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.ganeshgfx.projectmanagement.services.Notifications
import com.google.android.material.color.DynamicColors.applyToActivitiesIfAvailable
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        applyToActivitiesIfAvailable(this)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            Notifications.GENERAL,
            "General",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "General Notifications"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}