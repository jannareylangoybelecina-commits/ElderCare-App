package com.eldercare.app.notification

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Shared parsing for reminder alarm times (used by [DashboardViewModel] and [ReminderAlarmRestore]).
 */
object ReminderTimeUtils {

    private const val DAY_MS = 24L * 60L * 60L * 1000L

    /**
     * If [millis] is not after "now", advances by whole days until it is (same clock time each day).
     */
    fun ensureFutureMillis(millis: Long): Long {
        var t = millis
        val now = System.currentTimeMillis()
        while (t <= now) {
            t += DAY_MS
        }
        return t
    }

    /**
     * Next daily occurrence after a fired alarm (medication repeat).
     */
    fun nextDailyAfter(lastScheduledMillis: Long): Long {
        var next = lastScheduledMillis + DAY_MS
        val now = System.currentTimeMillis()
        while (next <= now) {
            next += DAY_MS
        }
        return next
    }

    fun parseTimeToTodayMillis(timeString: String): Long? {
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
     * Next time-of-day trigger for medication (today if still ahead, else a future day).
     */
    fun nextMedicationTriggerMillis(timeString: String): Long? {
        val target = parseTimeToTodayMillis(timeString) ?: return null
        return ensureFutureMillis(target)
    }

    fun parseDateTimeToMillis(dateString: String, timeString: String): Long? {
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

        return parseTimeToTodayMillis(timeString)
    }

    /**
     * Appointment at a specific calendar date/time. No alarm if that instant is already in the past.
     */
    fun nextAppointmentTriggerMillis(dateString: String, timeString: String): Long? {
        val raw = parseDateTimeToMillis(dateString, timeString) ?: return null
        return if (raw > System.currentTimeMillis()) raw else null
    }
}
