package com.ganeshgfx.projectmanagement.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import com.ganeshgfx.projectmanagement.models.Notice
import com.ganeshgfx.projectmanagement.repositories.MainRepository
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
    lateinit var repo: MainRepository

    @Inject
    lateinit var notifications: NotificationHelper

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        // initialize here
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = notifications.makeNotification(
            Notice("Project Management", "Running"),
            2
        )
        startForeground(
            2, notification
        )
        Toast.makeText(this, "Project Management Started", Toast.LENGTH_LONG).show()

        repo.notification.onEach {
            notifications.show(it)
        }.launchIn(scope)
        repo.startJob()
      //var count = 0
//        scope.launch(Dispatchers.IO) {
//            while (true) {
//                delay(1000)
//                count++
//                log("Running...",count)
//            }
//        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "Project Management Closed", Toast.LENGTH_LONG).show()
    }

}