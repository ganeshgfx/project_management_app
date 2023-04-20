package com.ganeshgfx.projectmanagement.services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.ganeshgfx.projectmanagement.MainActivity
import com.ganeshgfx.projectmanagement.R
import com.ganeshgfx.projectmanagement.models.Notice

class Notifications(private val context: Context) {

    private val manager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun show(notice: Notice) {
        val notice = makeNotification(notice, 1)

        manager.notify(
            (1..100).random(),
            notice
        )
    }

    fun makeNotification(notice: Notice, code: Int): Notification {
        val activityIntent = PendingIntent.getActivity(
            context,
            code,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val notice = NotificationCompat.Builder(context, GENERAL)
            .setSmallIcon(R.drawable.round_transit_enterexit_24)
            .setContentTitle(notice.title)
            .setContentText(notice.description)
            .setContentIntent(activityIntent)
            .build()
        return notice
    }

    companion object {
        const val GENERAL = "general"
    }
}