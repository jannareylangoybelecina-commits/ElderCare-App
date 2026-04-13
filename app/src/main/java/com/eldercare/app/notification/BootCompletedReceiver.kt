package com.eldercare.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        Log.d("BootCompletedReceiver", "Rescheduling reminder alarms after boot")
        ReminderAlarmRestore.rescheduleAllForCurrentUser(context)
    }
}
