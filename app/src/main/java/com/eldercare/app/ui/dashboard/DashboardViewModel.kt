package com.eldercare.app.ui.dashboard

import android.app.Application
import androidx.lifecycle.ViewModel
import com.eldercare.app.notification.ReminderScheduler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

data class ReminderItem(
    val id: String = "",
    val title: String = "",
    val timeString: String = "",
    val isCompleted: Boolean = false,
    val isMedication: Boolean = false,
    val dosage: String = "",
    val date: String = ""
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val application: Application
) : ViewModel() {

    private val _userName = MutableStateFlow("User")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userRole = MutableStateFlow("elderly")
    val userRole: StateFlow<String> = _userRole.asStateFlow()

    private val _reminders = MutableStateFlow<List<ReminderItem>>(emptyList())
    val reminders: StateFlow<List<ReminderItem>> = _reminders.asStateFlow()

    private val _healthReadings = MutableStateFlow<List<HealthReadingItem>>(emptyList())
    val healthReadings: StateFlow<List<HealthReadingItem>> = _healthReadings.asStateFlow()

    init {
        listenToUserData()
        listenToReminders()
        listenToHealthReadings()
    }

    private fun listenToUserData() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid).addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener
            _userName.value = snapshot.getString("fullName") ?: "User"
            _userRole.value = snapshot.getString("role") ?: "elderly"

            // If caregiver, listen to ALL elderly data
            if (_userRole.value == "caregiver") {
                listenToAllElderlyReminders()
                listenToAllElderlyHealthReadings()
            }
        }
    }

    // ── Elderly: listen to own reminders ─────────────────────
    private fun listenToReminders() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("reminders")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val items = snapshot.documents.mapNotNull { doc ->
                    val type = doc.getString("type") ?: "appointment"
                    ReminderItem(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        timeString = doc.getString("timeString") ?: "",
                        isCompleted = doc.getBoolean("isCompleted") ?: false,
                        isMedication = type == "medication",
                        dosage = doc.getString("dosage") ?: "",
                        date = doc.getString("date") ?: ""
                    )
                }
                _reminders.value = items
            }
    }

    // ── Caregiver: listen to ALL reminders (all elderly users) ─
    private fun listenToAllElderlyReminders() {
        firestore.collection("reminders")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val items = snapshot.documents.mapNotNull { doc ->
                    val type = doc.getString("type") ?: "appointment"
                    ReminderItem(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        timeString = doc.getString("timeString") ?: "",
                        isCompleted = doc.getBoolean("isCompleted") ?: false,
                        isMedication = type == "medication",
                        dosage = doc.getString("dosage") ?: "",
                        date = doc.getString("date") ?: ""
                    )
                }
                _reminders.value = items
            }
    }

    // ── Health Readings: own data ────────────────────────────
    private fun listenToHealthReadings() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("health_readings")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val items = snapshot.documents.mapNotNull { doc ->
                    HealthReadingItem(
                        id = doc.id,
                        systolic = doc.getString("systolic") ?: "",
                        diastolic = doc.getString("diastolic") ?: "",
                        date = doc.getString("date") ?: "",
                        weight = doc.getString("weight") ?: "",
                        heartRate = doc.getString("heartRate") ?: ""
                    )
                }
                _healthReadings.value = items.sortedByDescending { it.date }
            }
    }

    // ── Caregiver: listen to ALL health readings ────────────
    private fun listenToAllElderlyHealthReadings() {
        firestore.collection("health_readings")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val items = snapshot.documents.mapNotNull { doc ->
                    HealthReadingItem(
                        id = doc.id,
                        systolic = doc.getString("systolic") ?: "",
                        diastolic = doc.getString("diastolic") ?: "",
                        date = doc.getString("date") ?: "",
                        weight = doc.getString("weight") ?: "",
                        heartRate = doc.getString("heartRate") ?: ""
                    )
                }
                _healthReadings.value = items.sortedByDescending { it.date }
            }
    }

    // ── Save Health Reading ─────────────────────────────────
    fun saveHealthReading(systolic: String, diastolic: String, date: String, weight: String, heartRate: String) {
        val uid = auth.currentUser?.uid ?: return
        val readingData = mapOf(
            "userId" to uid,
            "systolic" to systolic,
            "diastolic" to diastolic,
            "date" to date,
            "weight" to weight,
            "heartRate" to heartRate,
            "timestamp" to com.google.firebase.Timestamp.now()
        )
        firestore.collection("health_readings").add(readingData)
    }

    // ── Set Medication (with local alarm scheduling) ─────────
    fun setMedication(name: String, dosage: String, time: String) {
        val uid = auth.currentUser?.uid ?: return
        val medicationData = mapOf(
            "userId" to uid,
            "title" to name,
            "dosage" to dosage,
            "timeString" to time,
            "type" to "medication",
            "isCompleted" to false,
            "timestamp" to com.google.firebase.Timestamp.now()
        )
        firestore.collection("reminders").add(medicationData)
            .addOnSuccessListener { docRef ->
                // Schedule a local notification
                val triggerTime = parseTimeToTodayMillis(time)
                if (triggerTime != null) {
                    ReminderScheduler.scheduleReminder(
                        context = application,
                        reminderId = docRef.id.hashCode(),
                        title = "Medication Reminder: $name",
                        message = "Time to take $name ($dosage). Don't forget!",
                        triggerTimeMillis = triggerTime,
                        isMedication = true
                    )
                }
            }
    }

    // ── Set Appointment (with local alarm scheduling) ────────
    fun setAppointment(title: String, date: String, time: String) {
        val uid = auth.currentUser?.uid ?: return
        val appointmentData = mapOf(
            "userId" to uid,
            "title" to title,
            "date" to date,
            "timeString" to time,
            "type" to "appointment",
            "isCompleted" to false,
            "timestamp" to com.google.firebase.Timestamp.now()
        )
        firestore.collection("reminders").add(appointmentData)
            .addOnSuccessListener { docRef ->
                // Schedule a local notification
                val triggerTime = parseDateTimeToMillis(date, time)
                if (triggerTime != null) {
                    ReminderScheduler.scheduleReminder(
                        context = application,
                        reminderId = docRef.id.hashCode(),
                        title = "Appointment Reminder",
                        message = "$title on $date at $time",
                        triggerTimeMillis = triggerTime,
                        isMedication = false
                    )
                }
            }
    }

    // ── Mark Medication as Taken ─────────────────────────────
    fun markReminderCompleted(reminderId: String) {
        firestore.collection("reminders").document(reminderId)
            .update("isCompleted", true)

        // Cancel the alarm since it's been completed
        ReminderScheduler.cancelReminder(application, reminderId.hashCode())
    }

    // ── Delete Actions ───────────────────────────────────────
    fun deleteHealthReading(id: String) {
        firestore.collection("health_readings").document(id).delete()
    }

    fun deleteReminder(id: String) {
        firestore.collection("reminders").document(id).delete()
        ReminderScheduler.cancelReminder(application, id.hashCode())
    }

    // ── Time Parsing Helpers ─────────────────────────────────

    /**
     * Parses a time string like "8:00 AM" or "14:00" to today's millis.
     */
    private fun parseTimeToTodayMillis(timeString: String): Long? {
        val cleanTime = timeString.replace("-", " ").trim()
        val formats = listOf(
            SimpleDateFormat("hh:mm a", Locale.US),
            SimpleDateFormat("HH:mm", Locale.US),
            SimpleDateFormat("h:mm a", Locale.US),
            SimpleDateFormat("hh:mma", Locale.US),
            SimpleDateFormat("h:mma", Locale.US)
        )

        for (format in formats) {
            try {
                val parsed = format.parse(cleanTime) ?: continue
                val parsedCal = Calendar.getInstance().apply { this.time = parsed }
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, parsedCal.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, parsedCal.get(Calendar.MINUTE))
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                return today.timeInMillis
            } catch (_: Exception) { }
        }
        return null
    }

    /**
     * Parses a date + time string to millis.
     * Supports formats like "January 1, 2026" + "8:00 AM"
     */
    private fun parseDateTimeToMillis(dateString: String, timeString: String): Long? {
        val cleanTime = timeString.replace("-", " ").trim()
        val cleanDate = dateString.trim()

        val dateFormats = listOf(
            SimpleDateFormat("MMMM d, yyyy", Locale.US),
            SimpleDateFormat("MM/dd/yyyy", Locale.US),
            SimpleDateFormat("yyyy-MM-dd", Locale.US)
        )
        val timeFormats = listOf(
            SimpleDateFormat("hh:mm a", Locale.US),
            SimpleDateFormat("HH:mm", Locale.US),
            SimpleDateFormat("h:mm a", Locale.US),
            SimpleDateFormat("hh:mma", Locale.US),
            SimpleDateFormat("h:mma", Locale.US)
        )

        var dateCal: Calendar? = null
        for (fmt in dateFormats) {
            try {
                val parsed = fmt.parse(cleanDate) ?: continue
                dateCal = Calendar.getInstance().apply { this.time = parsed }
                break
            } catch (_: Exception) { }
        }

        var timeCal: Calendar? = null
        for (fmt in timeFormats) {
            try {
                val parsed = fmt.parse(cleanTime) ?: continue
                timeCal = Calendar.getInstance().apply { this.time = parsed }
                break
            } catch (_: Exception) { }
        }

        if (dateCal != null && timeCal != null) {
            dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
            dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))
            dateCal.set(Calendar.SECOND, 0)
            return dateCal.timeInMillis
        }

        // Fallback: just try time-only
        return parseTimeToTodayMillis(timeString)
    }
}

data class HealthReadingItem(
    val id: String = "",
    val systolic: String = "",
    val diastolic: String = "",
    val date: String = "",
    val weight: String = "",
    val heartRate: String = ""
)
