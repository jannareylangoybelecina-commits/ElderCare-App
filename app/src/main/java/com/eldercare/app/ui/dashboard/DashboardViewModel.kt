package com.eldercare.app.ui.dashboard

import android.app.Application
import androidx.lifecycle.ViewModel
import com.eldercare.app.notification.ReminderScheduler
import com.eldercare.app.notification.ReminderTimeUtils
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
    val medicationStatus: String = "PENDING",
    val isMedication: Boolean = false,
    val dosage: String = "",
    val date: String = "",
    val userId: String = "" // Added to map back to user profile for Caregiver
)

data class HealthReadingItem(
    val id: String = "",
    val systolic: String = "",
    val diastolic: String = "",
    val date: String = "",
    val weight: String = "",
    val heartRate: String = "",
    val userId: String = "" // Added to map back to user profile for Caregiver
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val application: Application
) : ViewModel() {
    companion object {
        const val MED_STATUS_PENDING = "PENDING"
        const val MED_STATUS_DONE = "DONE"
        const val MED_STATUS_MISSED = "MISSED"
    }

    private val _userName = MutableStateFlow("User")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userRole = MutableStateFlow("elderly")
    val userRole: StateFlow<String> = _userRole.asStateFlow()

    // ── Mapping of userId to full name for Caregiver display ─
    private val _elderlyUsersMap = MutableStateFlow<Map<String, String>>(emptyMap())
    val elderlyUsersMap: StateFlow<Map<String, String>> = _elderlyUsersMap.asStateFlow()

    private val _reminders = MutableStateFlow<List<ReminderItem>>(emptyList())
    val reminders: StateFlow<List<ReminderItem>> = _reminders.asStateFlow()

    private val _healthReadings = MutableStateFlow<List<HealthReadingItem>>(emptyList())
    val healthReadings: StateFlow<List<HealthReadingItem>> = _healthReadings.asStateFlow()

    // ── Notification-filtered data ───────────────────────────
    private val _notificationHealthReadings = MutableStateFlow<List<HealthReadingItem>>(emptyList())
    val notificationHealthReadings: StateFlow<List<HealthReadingItem>> = _notificationHealthReadings.asStateFlow()

    private val _todayMissedMedications = MutableStateFlow<List<ReminderItem>>(emptyList())
    val todayMissedMedications: StateFlow<List<ReminderItem>> = _todayMissedMedications.asStateFlow()

    private val _unreadNotificationCount = MutableStateFlow(0)
    val unreadNotificationCount: StateFlow<Int> = _unreadNotificationCount.asStateFlow()

    // ── Health History grouped by month ──────────────────────
    private val _healthReadingsByMonth = MutableStateFlow<Map<String, List<HealthReadingItem>>>(emptyMap())
    val healthReadingsByMonth: StateFlow<Map<String, List<HealthReadingItem>>> = _healthReadingsByMonth.asStateFlow()

    /** Missed (incomplete) medications grouped by calendar month label, newest months first. */
    private val _missedMedicationsByMonth = MutableStateFlow<List<Pair<String, List<ReminderItem>>>>(emptyList())
    val missedMedicationsByMonth: StateFlow<List<Pair<String, List<ReminderItem>>>> =
        _missedMedicationsByMonth.asStateFlow()

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

            // If caregiver, gather list of all users to map names, and pull all data
            if (_userRole.value == "caregiver") {
                listenToAllElderlyUsers()
                listenToAllElderlyReminders()
                listenToAllElderlyHealthReadings()
            }
        }
    }

    private fun listenToAllElderlyUsers() {
        firestore.collection("users")
            .whereEqualTo("role", "elderly")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val map = mutableMapOf<String, String>()
                for (doc in snapshot.documents) {
                    val name = doc.getString("fullName") ?: "Elderly User"
                    map[doc.id] = name
                }
                _elderlyUsersMap.value = map
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
                        medicationStatus = resolveMedicationStatus(
                            type = type,
                            isCompleted = doc.getBoolean("isCompleted") ?: false,
                            storedStatus = doc.getString("medicationStatus")
                        ),
                        isMedication = type == "medication",
                        dosage = doc.getString("dosage") ?: "",
                        date = doc.getString("date") ?: "",
                        userId = doc.getString("userId") ?: ""
                    )
                }
                _reminders.value = items
                updateNotificationData()
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
                        medicationStatus = resolveMedicationStatus(
                            type = type,
                            isCompleted = doc.getBoolean("isCompleted") ?: false,
                            storedStatus = doc.getString("medicationStatus")
                        ),
                        isMedication = type == "medication",
                        dosage = doc.getString("dosage") ?: "",
                        date = doc.getString("date") ?: "",
                        userId = doc.getString("userId") ?: ""
                    )
                }
                _reminders.value = items
                updateNotificationData()
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
                        heartRate = doc.getString("heartRate") ?: "",
                        userId = doc.getString("userId") ?: ""
                    )
                }
                _healthReadings.value = items.sortedByDescending { it.date }
                updateNotificationData()
                updateHealthReadingsByMonth()
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
                        heartRate = doc.getString("heartRate") ?: "",
                        userId = doc.getString("userId") ?: ""
                    )
                }
                _healthReadings.value = items.sortedByDescending { it.date }
                updateNotificationData()
                updateHealthReadingsByMonth()
            }
    }

    // ── Update notification-specific filtered data ───────────
    private fun updateNotificationData() {
        // Health Readings are no longer displayed in standard Notifications feed.
        _notificationHealthReadings.value = emptyList()

        // Missed medications for today only (1-day span)
        val todayStr = SimpleDateFormat("MM/dd/yyyy", Locale.US).format(Calendar.getInstance().time)
        val todayFormatted = SimpleDateFormat("MMMM d, yyyy", Locale.US).format(Calendar.getInstance().time)
        
        val dateFormats = listOf(
            SimpleDateFormat("MMMM d, yyyy", Locale.US),
            SimpleDateFormat("MM/dd/yyyy", Locale.US),
            SimpleDateFormat("yyyy-MM-dd", Locale.US)
        )
        
        val todayMissed = _reminders.value.filter { reminder ->
            reminder.isMedication && reminder.medicationStatus == MED_STATUS_MISSED && (
                reminder.date.isBlank() || 
                reminder.date == todayStr || 
                reminder.date == todayFormatted ||
                isToday(reminder.date, dateFormats)
            )
        }
        _todayMissedMedications.value = todayMissed

        // Total notification count strictly equals today's missed medications
        _unreadNotificationCount.value = todayMissed.size

        updateMissedMedicationsByMonth()
    }

    private fun updateMissedMedicationsByMonth() {
        val dateFormats = listOf(
            SimpleDateFormat("MMMM d, yyyy", Locale.US),
            SimpleDateFormat("MM/dd/yyyy", Locale.US),
            SimpleDateFormat("yyyy-MM-dd", Locale.US)
        )
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.US)
        val monthParseFmt = SimpleDateFormat("MMMM yyyy", Locale.US)
        val allMissed = _reminders.value.filter {
            it.isMedication && it.medicationStatus == MED_STATUS_MISSED
        }
        val grouped = allMissed.groupBy { reminder ->
            parseDate(reminder.date, dateFormats)?.let { monthFormat.format(it) } ?: "Date unknown"
        }
        _missedMedicationsByMonth.value = grouped.entries
            .sortedByDescending { (label, _) ->
                runCatching { monthParseFmt.parse(label)?.time ?: 0L }.getOrDefault(0L)
            }
            .map { it.key to it.value }
    }

    // ── Group health readings by month ───────────────────────
    private fun updateHealthReadingsByMonth() {
        val dateFormats = listOf(
            SimpleDateFormat("MMMM d, yyyy", Locale.US),
            SimpleDateFormat("MM/dd/yyyy", Locale.US),
            SimpleDateFormat("yyyy-MM-dd", Locale.US)
        )
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.US)
        
        val grouped = _healthReadings.value.groupBy { reading ->
            val parsed = parseDate(reading.date, dateFormats)
            if (parsed != null) {
                monthFormat.format(parsed)
            } else {
                "Unknown"
            }
        }.toSortedMap(compareByDescending { it })
        
        _healthReadingsByMonth.value = grouped
    }

    private fun parseDate(dateString: String, formats: List<SimpleDateFormat>): java.util.Date? {
        for (fmt in formats) {
            try {
                return fmt.parse(dateString.trim())
            } catch (_: Exception) { }
        }
        return null
    }

    private fun isToday(dateString: String, formats: List<SimpleDateFormat>): Boolean {
        val parsed = parseDate(dateString, formats) ?: return false
        val todayCal = Calendar.getInstance()
        val parsedCal = Calendar.getInstance().apply { time = parsed }
        return todayCal.get(Calendar.YEAR) == parsedCal.get(Calendar.YEAR) &&
               todayCal.get(Calendar.DAY_OF_YEAR) == parsedCal.get(Calendar.DAY_OF_YEAR)
    }

    // ── Save Health Reading ─────────────────────────────────
    fun saveHealthReading(systolic: String, diastolic: String, date: String, weight: String, heartRate: String) {
        val uid = auth.currentUser?.uid ?: return
        
        // Format checking to restrict only 1 entry per calendar month
        val dateFormats = listOf(
            SimpleDateFormat("MMMM d, yyyy", Locale.US),
            SimpleDateFormat("MM/dd/yyyy", Locale.US),
            SimpleDateFormat("yyyy-MM-dd", Locale.US)
        )
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.US)
        
        val parsedNewDate = parseDate(date, dateFormats)
        val targetMonthStr = if (parsedNewDate != null) monthFormat.format(parsedNewDate) else "Unknown"

        val existingReadingsForMonth = _healthReadings.value.filter { reading ->
            val parsed = parseDate(reading.date, dateFormats)
            val monthStr = if (parsed != null) monthFormat.format(parsed) else "Unknown"
            monthStr == targetMonthStr && reading.userId == uid
        }

        // Execute deletions recursively to enforce uniqueness manually
        existingReadingsForMonth.forEach { oldReading ->
            firestore.collection("health_readings").document(oldReading.id).delete()
        }

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
        val todayDate = SimpleDateFormat("MMMM d, yyyy", Locale.US).format(Calendar.getInstance().time)
        val medicationData = mapOf(
            "userId" to uid,
            "title" to name,
            "dosage" to dosage,
            "timeString" to time,
            "date" to todayDate,
            "type" to "medication",
            "isCompleted" to false,
            "medicationStatus" to MED_STATUS_PENDING,
            "timestamp" to com.google.firebase.Timestamp.now()
        )
        firestore.collection("reminders").add(medicationData)
            .addOnSuccessListener { docRef ->
                val triggerTime = ReminderTimeUtils.nextMedicationTriggerMillis(time)
                if (triggerTime != null) {
                    ReminderScheduler.scheduleReminder(
                        context = application,
                        alarmRequestCode = docRef.id.hashCode(),
                        firestoreReminderId = docRef.id,
                        title = "Medication Reminder: $name",
                        message = "Time to take $name ($dosage). Don't forget!",
                        triggerTimeMillis = triggerTime,
                        isMedication = true,
                        lastScheduledAtMillis = triggerTime
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
                val triggerTime = ReminderTimeUtils.nextAppointmentTriggerMillis(date, time)
                if (triggerTime != null) {
                    ReminderScheduler.scheduleReminder(
                        context = application,
                        alarmRequestCode = docRef.id.hashCode(),
                        firestoreReminderId = docRef.id,
                        title = "Appointment Reminder",
                        message = "$title on $date at $time",
                        triggerTimeMillis = triggerTime,
                        isMedication = false,
                        lastScheduledAtMillis = triggerTime
                    )
                }
            }
    }

    // ── Mark Medication as Taken ─────────────────────────────
    fun markReminderCompleted(reminderId: String) {
        firestore.collection("reminders").document(reminderId)
            .update(
                mapOf(
                    "isCompleted" to true,
                    "medicationStatus" to MED_STATUS_DONE
                )
            )

        // Cancel the alarm since it's been completed
        ReminderScheduler.cancelReminder(application, reminderId.hashCode())
    }

    fun markMedicationMissed(reminderId: String) {
        firestore.collection("reminders").document(reminderId)
            .update(
                mapOf(
                    "isCompleted" to false,
                    "medicationStatus" to MED_STATUS_MISSED
                )
            )

        // Cancel the alarm to prevent repeat reminders once explicitly missed.
        ReminderScheduler.cancelReminder(application, reminderId.hashCode())
    }

    private fun resolveMedicationStatus(type: String, isCompleted: Boolean, storedStatus: String?): String {
        if (type != "medication") return MED_STATUS_PENDING
        return when {
            !storedStatus.isNullOrBlank() -> storedStatus
            isCompleted -> MED_STATUS_DONE
            else -> MED_STATUS_PENDING
        }
    }

    // ── Delete Actions ───────────────────────────────────────
    fun deleteHealthReading(id: String) {
        firestore.collection("health_readings").document(id).delete()
    }

    fun deleteReminder(id: String) {
        firestore.collection("reminders").document(id).delete()
        ReminderScheduler.cancelReminder(application, id.hashCode())
    }

}
