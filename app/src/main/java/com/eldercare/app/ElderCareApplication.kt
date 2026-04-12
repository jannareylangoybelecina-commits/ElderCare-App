package com.eldercare.app

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.eldercare.app.notification.MissedMedicationWorker
import com.eldercare.app.notification.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

/**
 * ElderCare Application class.
 * @HiltAndroidApp triggers Hilt's code generation,
 * including a base class for the application that serves
 * as the application-level dependency container.
 *
 * Firebase is auto-initialized via google-services plugin
 * and the google-services.json file placed in app/.
 */
@HiltAndroidApp
class ElderCareApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Create notification channels
        NotificationHelper.createNotificationChannels(this)

        // Schedule periodic missed medication check (every 30 minutes)
        val missedMedWork = PeriodicWorkRequestBuilder<MissedMedicationWorker>(
            30, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            MissedMedicationWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            missedMedWork
        )
    }
}
