package com.eldercare.app.ui.dashboard

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.eldercare.app.ui.theme.ElderCareTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReminderAlertActivity : ComponentActivity() {

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

        val reminderId = intent.getStringExtra("EXTRA_REMINDER_ID") ?: ""

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
}
