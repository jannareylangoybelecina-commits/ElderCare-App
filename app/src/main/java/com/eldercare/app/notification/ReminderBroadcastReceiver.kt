package com.eldercare.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * BroadcastReceiver triggered by AlarmManager at the scheduled time.
 * Extracts the reminder details from the intent and shows a local notification.
 */
class ReminderBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_MESSAGE = "extra_message"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
        const val EXTRA_IS_MEDICATION = "extra_is_medication"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "ElderCare Reminder"
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "You have a pending reminder"
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)
        val isMedication = intent.getBooleanExtra(EXTRA_IS_MEDICATION, false)

        val channelId = if (isMedication) {
            NotificationHelper.CHANNEL_MEDICATION
        } else {
            NotificationHelper.CHANNEL_APPOINTMENT
        }

        val actionIntent = Intent(context, com.eldercare.app.ui.dashboard.ReminderAlertActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("EXTRA_REMINDER_ID", notificationId.toString())
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            context,
            notificationId,
            actionIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        NotificationHelper.showNotification(
            context = context,
            title = title,
            message = message,
            notificationId = notificationId,
            channelId = channelId,
            fullScreenIntent = pendingIntent
        )
    }
}
