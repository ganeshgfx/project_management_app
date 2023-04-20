package com.ganeshgfx.projectmanagement.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.models.Notice
import com.ganeshgfx.projectmanagement.repositories.MainActivityRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class MainServices() : Service() {

    @Inject
    lateinit var scope: CoroutineScope

    @Inject
    lateinit var repo: MainActivityRepository

    @Inject
    lateinit var notifications: Notifications

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        // initialize Firestore here
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        val notification = notifications.makeNotification(
//            Notice("Project Management", "Running"),
//            2
//        )
//        startForeground(
//            2, notification
//        )
        repo.notification.onEach {
            notifications.show(it)
        }.launchIn(scope)
        repo.startJob()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // clean up any resources here
    }

}