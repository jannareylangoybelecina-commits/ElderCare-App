package com.eldercare.app.ui.dashboard

import android.app.KeyguardManager
import android.content.Context
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.eldercare.app.notification.NotificationPreferenceStore
import com.eldercare.app.ui.theme.ElderCareTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReminderAlertActivity : ComponentActivity() {

    companion object {
        const val EXTRA_REMINDER_DOC_ID = "extra_reminder_doc_id"
    }

    private var ringtone: Ringtone? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Force screen on and wake up lockscreen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }

        val reminderId = intent.getStringExtra(EXTRA_REMINDER_DOC_ID) ?: ""

        setContent {
            ElderCareTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ReminderAlertScreen(
                        reminderId = reminderId,
                        onNavigateBack = { finish() }
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        startAlertSoundIfEnabled()
    }

    override fun onStop() {
        stopAlertSound()
        super.onStop()
    }

    private fun startAlertSoundIfEnabled() {
        if (!NotificationPreferenceStore.soundEnabled(this)) return

        val uri = NotificationPreferenceStore.alarmToneUri(this)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        val r = runCatching { RingtoneManager.getRingtone(this, uri) }.getOrNull() ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            r.audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            r.isLooping = true
        }
        ringtone = r
        runCatching { r.play() }
    }

    private fun stopAlertSound() {
        ringtone?.let { runCatching { it.stop() } }
        ringtone = null
    }
}
