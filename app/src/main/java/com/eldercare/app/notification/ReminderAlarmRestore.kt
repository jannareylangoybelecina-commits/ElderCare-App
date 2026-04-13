package com.eldercare.app.notification

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Re-schedules alarms from Firestore after boot or app start (AlarmManager alarms do not survive reboot).
 */
object ReminderAlarmRestore {

    private const val TAG = "ReminderAlarmRestore"

    fun rescheduleAllForCurrentUser(context: Context) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val app = context.applicationContext

        FirebaseFirestore.getInstance().collection("reminders")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot.documents) {
                    if (doc.getBoolean("isCompleted") == true) continue

                    val type = doc.getString("type") ?: "appointment"
                    val isMedication = type == "medication"
                    val timeStr = doc.getString("timeString") ?: continue
                    val title = doc.getString("title") ?: "Reminder"
                    val dosage = doc.getString("dosage") ?: ""
                    val dateStr = doc.getString("date") ?: ""

                    val trigger = if (isMedication) {
                        ReminderTimeUtils.nextMedicationTriggerMillis(timeStr)
                    } else {
                        ReminderTimeUtils.nextAppointmentTriggerMillis(dateStr, timeStr)
                    }

                    if (trigger == null) {
                        Log.d(TAG, "No future trigger for ${doc.id}")
                        continue
                    }

                    val message = if (isMedication) {
                        "Time to take $title ($dosage). Don't forget!"
                    } else {
                        "$title on $dateStr at $timeStr"
                    }

                    val notifTitle = if (isMedication) "Medication Reminder: $title" else "Appointment Reminder"

                    ReminderScheduler.scheduleReminder(
                        context = app,
                        alarmRequestCode = doc.id.hashCode(),
                        firestoreReminderId = doc.id,
                        title = notifTitle,
                        message = message,
                        triggerTimeMillis = trigger,
                        isMedication = isMedication,
                        lastScheduledAtMillis = trigger
                    )
                }
                Log.d(TAG, "Reschedule pass complete (${snapshot.documents.size} docs)")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to reschedule reminders", e)
            }
    }
}
