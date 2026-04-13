package com.eldercare.app.notification

import android.content.Context
import android.net.Uri

/**
 * Local copy of notification / alarm UI settings so [ReminderBroadcastReceiver] and
 * [NotificationHelper] can read them without Firestore.
 */
object NotificationPreferenceStore {

    private const val PREFS = "elder_notification_prefs"
    private const val KEY_ALARM_URI = "alarm_tone_uri"
    private const val KEY_SOUND = "sound_enabled"
    private const val KEY_VIBRATE = "vibration_enabled"

    fun sync(
        context: Context,
        alarmToneUri: String?,
        soundEnabled: Boolean,
        vibrationEnabled: Boolean
    ) {
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_ALARM_URI, alarmToneUri)
            .putBoolean(KEY_SOUND, soundEnabled)
            .putBoolean(KEY_VIBRATE, vibrationEnabled)
            .apply()
    }

    fun alarmToneUriString(context: Context): String? =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_ALARM_URI, null)

    fun alarmToneUri(context: Context): Uri? =
        alarmToneUriString(context)?.let { runCatching { Uri.parse(it) }.getOrNull() }

    fun soundEnabled(context: Context): Boolean =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_SOUND, true)

    fun vibrationEnabled(context: Context): Boolean =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_VIBRATE, true)
}
