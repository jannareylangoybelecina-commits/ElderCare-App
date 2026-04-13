package com.eldercare.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.eldercare.app.R

object NotificationHelper {

    const val CHANNEL_MEDICATION = "medication_reminders"
    const val CHANNEL_APPOINTMENT = "appointment_reminders"
    const val CHANNEL_MISSED_MED = "missed_medication_alerts"

    fun createNotificationChannels(context: Context) {
        refreshChannelsFromPrefs(context.applicationContext)
    }

    /**
     * Recreates reminder channels with the current alarm tone / sound / vibration prefs.
     */
    fun refreshChannelsFromPrefs(context: Context) {
        val app = context.applicationContext
        val notificationManager =
            app.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val soundUri: Uri? = if (NotificationPreferenceStore.soundEnabled(app)) {
            NotificationPreferenceStore.alarmToneUri(app)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        } else {
            null
        }

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        // Drop channels first so sound / URI changes take effect (O+ channels are otherwise mostly immutable).
        listOf(CHANNEL_MEDICATION, CHANNEL_APPOINTMENT, CHANNEL_MISSED_MED).forEach { id ->
            notificationManager.deleteNotificationChannel(id)
        }

        val importance =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NotificationManager.IMPORTANCE_MAX
            } else {
                NotificationManager.IMPORTANCE_HIGH
            }

        fun buildChannel(
            id: String,
            name: String,
            description: String
        ): NotificationChannel {
            val ch = NotificationChannel(
                id,
                name,
                importance
            ).apply {
                this.description = description
                enableVibration(NotificationPreferenceStore.vibrationEnabled(app))
                if (soundUri != null) {
                    setSound(soundUri, audioAttributes)
                } else {
                    setSound(null, null)
                }
            }
            return ch
        }

        notificationManager.createNotificationChannel(
            buildChannel(
                CHANNEL_MEDICATION,
                "Medication Reminders",
                "Reminders to take your medication on time"
            )
        )
        notificationManager.createNotificationChannel(
            buildChannel(
                CHANNEL_APPOINTMENT,
                "Appointment Reminders",
                "Reminders for upcoming appointments"
            )
        )
        notificationManager.createNotificationChannel(
            buildChannel(
                CHANNEL_MISSED_MED,
                "Missed Medication Alerts",
                "Alerts when a medication dose is missed"
            )
        )
    }

    fun showNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int,
        channelId: String = CHANNEL_MEDICATION,
        fullScreenIntent: android.app.PendingIntent? = null
    ) {
        val app = context.applicationContext
        val notificationManager =
            app.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val soundOn = NotificationPreferenceStore.soundEnabled(app)

        val builder = NotificationCompat.Builder(app, channelId)
            .setSmallIcon(R.drawable.eldercare_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setOnlyAlertOnce(false)

        if (!soundOn) {
            builder.setSilent(true)
        }

        if (NotificationPreferenceStore.vibrationEnabled(app)) {
            builder.setVibrate(longArrayOf(0, 500, 200, 500))
        } else {
            builder.setVibrate(null)
        }

        if (fullScreenIntent != null) {
            builder.setFullScreenIntent(fullScreenIntent, true)
        }

        notificationManager.notify(notificationId, builder.build())
    }
}
