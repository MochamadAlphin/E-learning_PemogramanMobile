package com.example.elearning

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat

class Notif : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val title = intent?.getStringExtra("titleExtra") ?: "Default Title"
        val message = intent?.getStringExtra("messageExtra") ?: "Default Message"

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = 1 // Unique ID for the notification

        val notificationSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(context, "NotifChannel")
            .setSmallIcon(R.drawable.ic_notification) // Ensure this icon exists
            .setContentTitle(title)
            .setContentText(message)
            .setSound(notificationSound)
            .setAutoCancel(true)

        notificationManager.notify(notificationID, notificationBuilder.build())
    }
}