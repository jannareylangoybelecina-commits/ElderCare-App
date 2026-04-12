package com.eldercare.app.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

/**
 * Schedules and cancels local alarm-based reminders using AlarmManager.
 */
object ReminderScheduler {

    private const val TAG = "ReminderScheduler"

    /**
     * Schedules a reminder notification at the specified time.
     *
     * @param context Application context
     * @param reminderId Unique ID for this reminder (used for PendingIntent request code)
     * @param title Title of the reminder notification
     * @param message Body text of the notification
     * @param triggerTimeMillis Time in millis since epoch when the alarm should fire
     * @param isMedication Whether this is a medication reminder (affects channel)
     */
    fun scheduleReminder(
        context: Context,
        reminderId: Int,
        title: String,
        message: String,
        triggerTimeMillis: Long,
        isMedication: Boolean = false
    ) {
        // Don't schedule if the time is in the past
        if (triggerTimeMillis <= System.currentTimeMillis()) {
            Log.d(TAG, "Skipping past reminder: $title")
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra(ReminderBroadcastReceiver.EXTRA_TITLE, title)
            putExtra(ReminderBroadcastReceiver.EXTRA_MESSAGE, message)
            putExtra(ReminderBroadcastReceiver.EXTRA_NOTIFICATION_ID, reminderId)
            putExtra(ReminderBroadcastReceiver.EXTRA_IS_MEDICATION, isMedication)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMillis,
                pendingIntent
            )
            Log.d(TAG, "Scheduled reminder: $title at $triggerTimeMillis")
        } catch (e: SecurityException) {
            // Fallback to inexact alarm if exact alarm permission is denied
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMillis,
                pendingIntent
            )
            Log.w(TAG, "Using inexact alarm as fallback: $title", e)
        }
    }

    /**
     * Cancels a previously scheduled reminder.
     */
    fun cancelReminder(context: Context, reminderId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "Cancelled reminder: $reminderId")
    }
}
