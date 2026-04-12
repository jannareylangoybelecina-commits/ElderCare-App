package com.eldercare.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.eldercare.app.R

/**
 * Helper class for creating and showing local notifications.
 */
object NotificationHelper {

    const val CHANNEL_MEDICATION = "medication_reminders"
    const val CHANNEL_APPOINTMENT = "appointment_reminders"
    const val CHANNEL_MISSED_MED = "missed_medication_alerts"

    /**
     * Creates all notification channels. Must be called on app startup.
     */
    fun createNotificationChannels(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val medicationChannel = NotificationChannel(
            CHANNEL_MEDICATION,
            "Medication Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminders to take your medication on time"
            enableVibration(true)
        }

        val appointmentChannel = NotificationChannel(
            CHANNEL_APPOINTMENT,
            "Appointment Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminders for upcoming appointments"
            enableVibration(true)
        }

        val missedMedChannel = NotificationChannel(
            CHANNEL_MISSED_MED,
            "Missed Medication Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Alerts when a medication dose is missed"
            enableVibration(true)
        }

        notificationManager.createNotificationChannel(medicationChannel)
        notificationManager.createNotificationChannel(appointmentChannel)
        notificationManager.createNotificationChannel(missedMedChannel)
    }

    /**
     * Shows a local notification.
     */
    fun showNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int,
        channelId: String = CHANNEL_MEDICATION,
        fullScreenIntent: android.app.PendingIntent? = null
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.eldercare_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX) // Required for full-screen intent
            .setAutoCancel(true)

        if (fullScreenIntent != null) {
            builder.setFullScreenIntent(fullScreenIntent, true)
        }

        notificationManager.notify(notificationId, builder.build())
    }
}
