package com.lieruce.realestatemanager.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.lieruce.realestatemanager.R

/**
 * NotificationHelper manages system notification channel creation and posting
 * for real estate property creation and update events.
 */
object NotificationHelper {

    private const val CHANNEL_ID = "property_notifications"
    private const val CHANNEL_NAME = "Property Notifications"

    /**
     * Initializes the NotificationChannel required on Android 8.0+ (API 26+).
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for property creation and update events"
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Posts an Android system notification when a property is saved or updated.
     */
    fun sendPropertyNotification(context: Context, title: String, message: String) {
        createNotificationChannel(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(
                System.currentTimeMillis().toInt(),
                builder.build()
            )
        } catch (e: SecurityException) {
            // Guard against runtime notification permission security exceptions on Android 13+
            e.printStackTrace()
        }
    }
}
