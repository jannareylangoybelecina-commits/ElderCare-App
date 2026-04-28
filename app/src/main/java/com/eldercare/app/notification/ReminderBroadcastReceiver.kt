package com.eldercare.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * BroadcastReceiver triggered by AlarmManager at the scheduled time.
 */
class ReminderBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_MESSAGE = "extra_message"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
        const val EXTRA_IS_MEDICATION = "extra_is_medication"
        const val EXTRA_FIRESTORE_REMINDER_ID = "extra_firestore_reminder_id"
        const val EXTRA_LAST_SCHEDULED_AT = "extra_last_scheduled_at"

        private const val TAG = "ReminderReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "ElderCare Reminder"
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "You have a pending reminder"
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)
        val isMedication = intent.getBooleanExtra(EXTRA_IS_MEDICATION, false)
        val firestoreReminderId = intent.getStringExtra(EXTRA_FIRESTORE_REMINDER_ID).orEmpty()
        val lastScheduledAt = intent.getLongExtra(EXTRA_LAST_SCHEDULED_AT, System.currentTimeMillis())

        val channelId = if (isMedication) {
            NotificationHelper.CHANNEL_MEDICATION
        } else {
            NotificationHelper.CHANNEL_APPOINTMENT
        }

        val actionIntent = Intent(context, com.eldercare.app.ui.dashboard.ReminderAlertActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(com.eldercare.app.ui.dashboard.ReminderAlertActivity.EXTRA_REMINDER_DOC_ID, firestoreReminderId)
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            context,
            notificationId,
            actionIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val shouldNotify = shouldSendReminderNotification(
                    firestoreReminderId = firestoreReminderId,
                    isMedication = isMedication
                )

                if (shouldNotify) {
                    NotificationHelper.showNotification(
                        context = context.applicationContext,
                        title = title,
                        message = message,
                        notificationId = notificationId,
                        channelId = channelId,
                        fullScreenIntent = pendingIntent
                    )

                    // Some OEMs/Android versions suppress full-screen intents or channel sounds.
                    // Starting the alert UI directly ensures the selected ringtone is played.
                    if (NotificationPreferenceStore.soundEnabled(context.applicationContext)) {
                        runCatching { context.startActivity(actionIntent) }
                    }
                }

                // Daily medication: schedule next occurrence (appointments are one-shot).
                if (isMedication && firestoreReminderId.isNotEmpty()) {
                    val nextTrigger = ReminderTimeUtils.nextDailyAfter(lastScheduledAt)
                    ReminderScheduler.scheduleReminder(
                        context = context.applicationContext,
                        alarmRequestCode = firestoreReminderId.hashCode(),
                        firestoreReminderId = firestoreReminderId,
                        title = title,
                        message = message,
                        triggerTimeMillis = nextTrigger,
                        isMedication = true,
                        lastScheduledAtMillis = nextTrigger
                    )
                    Log.d(TAG, "Chained next medication alarm at $nextTrigger for $firestoreReminderId")
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun shouldSendReminderNotification(
        firestoreReminderId: String,
        isMedication: Boolean
    ): Boolean {
        if (firestoreReminderId.isBlank()) return true
        return try {
            val firestore = FirebaseFirestore.getInstance()
            val reminderDoc = firestore.collection("reminders")
                .document(firestoreReminderId)
                .get()
                .await()

            val userId = reminderDoc.getString("userId").orEmpty()
            if (userId.isBlank()) return true

            val key = if (isMedication) "missedMedicationAlerts" else "appointmentReminders"
            val defaultValue = true

            val settingsDoc = firestore.collection("users")
                .document(userId)
                .collection("settings")
                .document("notifications")
                .get()
                .await()
            settingsDoc.getBoolean(key) ?: defaultValue
        } catch (_: Exception) {
            true
        }
    }
}
