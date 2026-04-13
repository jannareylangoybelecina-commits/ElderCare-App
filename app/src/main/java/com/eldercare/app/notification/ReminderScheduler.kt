package com.eldercare.app.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Schedules and cancels local alarm-based reminders using AlarmManager.
 */
object ReminderScheduler {

    private const val TAG = "ReminderScheduler"

    /**
     * @param alarmRequestCode Stable int for [PendingIntent] identity (typically [String.hashCode] of Firestore doc id).
     * @param firestoreReminderId Real document id for the reminder alert UI and completion.
     * @param lastScheduledAtMillis Wall time this alarm was scheduled for; used to chain daily medication repeats.
     */
    fun scheduleReminder(
        context: Context,
        alarmRequestCode: Int,
        firestoreReminderId: String,
        title: String,
        message: String,
        triggerTimeMillis: Long,
        isMedication: Boolean = false,
        lastScheduledAtMillis: Long = triggerTimeMillis
    ) {
        if (triggerTimeMillis <= System.currentTimeMillis()) {
            Log.d(TAG, "Skipping past reminder: $title at $triggerTimeMillis")
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra(ReminderBroadcastReceiver.EXTRA_TITLE, title)
            putExtra(ReminderBroadcastReceiver.EXTRA_MESSAGE, message)
            putExtra(ReminderBroadcastReceiver.EXTRA_NOTIFICATION_ID, alarmRequestCode)
            putExtra(ReminderBroadcastReceiver.EXTRA_FIRESTORE_REMINDER_ID, firestoreReminderId)
            putExtra(ReminderBroadcastReceiver.EXTRA_IS_MEDICATION, isMedication)
            putExtra(ReminderBroadcastReceiver.EXTRA_LAST_SCHEDULED_AT, lastScheduledAtMillis)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmRequestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMillis,
                pendingIntent
            )
            Log.d(TAG, "Scheduled reminder doc=$firestoreReminderId at $triggerTimeMillis")
        } catch (e: SecurityException) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMillis,
                pendingIntent
            )
            Log.w(TAG, "Exact alarm not allowed; used setAndAllowWhileIdle: $title", e)
        }
    }

    fun cancelReminder(context: Context, alarmRequestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmRequestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "Cancelled reminder alarm: $alarmRequestCode")
    }
}
