package com.eldercare.app.notification

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * WorkManager periodic worker that checks for overdue/missed medications.
 * Runs every 30 minutes and flags medications that have passed their
 * scheduled time without being marked as completed.
 */
class MissedMedicationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val TAG = "MissedMedicationWorker"
        const val WORK_NAME = "missed_medication_check"
    }

    override suspend fun doWork(): Result {
        return try {
            checkForMissedMedications()
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error checking missed medications", e)
            Result.retry()
        }
    }

    private suspend fun checkForMissedMedications() {
        val firestore = FirebaseFirestore.getInstance()

        // Query all medication reminders that are not completed yet.
        val snapshot = firestore.collection("reminders")
            .whereEqualTo("type", "medication")
            .whereEqualTo("isCompleted", false)
            .get()
            .await()

        if (snapshot.isEmpty) {
            Log.d(TAG, "No pending medications found")
            return
        }

        val now = Calendar.getInstance()
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
        val timeFormat24 = SimpleDateFormat("HH:mm", Locale.US)

        var missedCount = 0

        for (doc in snapshot.documents) {
            val status = doc.getString("medicationStatus")
            if (status == "DONE" || status == "MISSED") continue

            val timeString = doc.getString("timeString") ?: continue
            val title = doc.getString("title") ?: "Medication"
            val userId = doc.getString("userId").orEmpty()

            // Try to parse the scheduled time
            val scheduledTime = try {
                timeFormat.parse(timeString)
                    ?: timeFormat24.parse(timeString)
                    ?: continue
            } catch (e: Exception) {
                continue
            }

            // Create a calendar for today at the scheduled time
            val scheduledCal = Calendar.getInstance().apply {
                val parsed = Calendar.getInstance().apply { time = scheduledTime }
                set(Calendar.HOUR_OF_DAY, parsed.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, parsed.get(Calendar.MINUTE))
                set(Calendar.SECOND, 0)
            }

            // If scheduled time has passed by more than 15 minutes, it's missed
            if (now.timeInMillis > scheduledCal.timeInMillis + 15 * 60 * 1000) {
                missedCount++
                firestore.collection("reminders")
                    .document(doc.id)
                    .update("medicationStatus", "MISSED")
                    .await()

                val shouldNotify = if (userId.isNotBlank()) {
                    isToggleEnabled(
                        firestore = firestore,
                        userId = userId,
                        key = "missedMedicationAlerts",
                        defaultValue = true
                    )
                } else {
                    true
                }

                // Keep health history records regardless of alert preference.
                if (shouldNotify) {
                    val notificationId = doc.id.hashCode()
                    NotificationHelper.showNotification(
                        context = context,
                        title = "Missed Dose: $title",
                        message = "You missed your $title scheduled at $timeString. " +
                                "Please take it as soon as possible or consult your caregiver.",
                        notificationId = notificationId,
                        channelId = NotificationHelper.CHANNEL_MISSED_MED
                    )
                }

                Log.d(TAG, "Missed medication detected: $title at $timeString")
            }
        }

        if (missedCount > 0) {
            Log.d(TAG, "Total missed medications: $missedCount")
        }
    }

    private suspend fun isToggleEnabled(
        firestore: FirebaseFirestore,
        userId: String,
        key: String,
        defaultValue: Boolean
    ): Boolean {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("settings")
                .document("notifications")
                .get()
                .await()
            snapshot.getBoolean(key) ?: defaultValue
        } catch (_: Exception) {
            defaultValue
        }
    }
}
