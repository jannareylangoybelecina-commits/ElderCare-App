package com.eldercare.app.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import com.eldercare.app.notification.NotificationHelper
import com.eldercare.app.notification.NotificationPreferenceStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserProfile(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val birthday: String = "",
    val gender: String = "",
    val address: String = ""
)

data class NotificationSettings(
    val readingResult: Boolean = true,
    val missedMedication: Boolean = true,
    val appointmentReminders: Boolean = true,
    val sound: Boolean = true,
    val vibration: Boolean = true,
    val alarmToneUri: String? = null // New field for alarm tone
)

data class PrivacySettings(
    val hideHealthReadings: Boolean = false,
    val hideNotificationPreviews: Boolean = false,
    val shareMissedMedication: Boolean = true,
    val shareAbnormalReading: Boolean = true
)

data class CaregiverInfo(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val missedMedsAlert: Boolean = true,
    val abnormalReadingsAlert: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _notificationSettings = MutableStateFlow(NotificationSettings())
    val notificationSettings: StateFlow<NotificationSettings> = _notificationSettings.asStateFlow()

    private val _privacySettings = MutableStateFlow(PrivacySettings())
    val privacySettings: StateFlow<PrivacySettings> = _privacySettings.asStateFlow()

    private val _caregiverInfo = MutableStateFlow<CaregiverInfo?>(null)
    val caregiverInfo: StateFlow<CaregiverInfo?> = _caregiverInfo.asStateFlow()

    init {
        listenToProfile()
        listenToSettings()
        listenToPrivacySettings()
        listenToCaregiver()
    }

    private fun listenToProfile() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid).addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener
            _userProfile.value = UserProfile(
                fullName = snapshot.getString("fullName") ?: "",
                email = snapshot.getString("email") ?: auth.currentUser?.email ?: "",
                phone = snapshot.getString("phone") ?: "",
                birthday = snapshot.getString("birthday") ?: "",
                gender = snapshot.getString("gender") ?: "",
                address = snapshot.getString("address") ?: ""
            )
        }
    }

    private fun listenToSettings() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid).collection("settings").document("notifications")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener
                val alarmUri = snapshot.getString("alarmToneUri")
                val soundOn = snapshot.getBoolean("sound") ?: true
                val vibrateOn = snapshot.getBoolean("vibration") ?: true
                _notificationSettings.value = NotificationSettings(
                    readingResult = snapshot.getBoolean("readingResultNotifications") ?: true,
                    missedMedication = snapshot.getBoolean("missedMedicationAlerts") ?: true,
                    appointmentReminders = snapshot.getBoolean("appointmentReminders") ?: true,
                    sound = soundOn,
                    vibration = vibrateOn,
                    alarmToneUri = alarmUri
                )
                NotificationPreferenceStore.sync(appContext, alarmUri, soundOn, vibrateOn)
                NotificationHelper.refreshChannelsFromPrefs(appContext)
            }
    }

    private fun listenToPrivacySettings() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid).collection("settings").document("privacy")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener
                _privacySettings.value = PrivacySettings(
                    hideHealthReadings = snapshot.getBoolean("hideHealthReadings") ?: false,
                    hideNotificationPreviews = snapshot.getBoolean("hideNotificationPreviews") ?: false,
                    shareMissedMedication = snapshot.getBoolean("shareMissedMedication") ?: true,
                    shareAbnormalReading = snapshot.getBoolean("shareAbnormalReading") ?: true
                )
            }
    }

    private fun listenToCaregiver() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid).collection("caregivers").limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || snapshot.isEmpty) {
                    _caregiverInfo.value = null
                    return@addSnapshotListener
                }
                val doc = snapshot.documents.first()
                _caregiverInfo.value = CaregiverInfo(
                    id = doc.id,
                    name = doc.getString("caregiverName") ?: "",
                    phone = doc.getString("phone") ?: "",
                    missedMedsAlert = doc.getBoolean("receiveMissedMedsAlerts") ?: true,
                    abnormalReadingsAlert = doc.getBoolean("receiveAbnormalReadingAlerts") ?: true
                )
            }
    }

    fun updateProfile(profile: UserProfile) {
        val uid = auth.currentUser?.uid ?: return
        val updates = mapOf(
            "fullName" to profile.fullName,
            "phone" to profile.phone,
            "birthday" to profile.birthday,
            "gender" to profile.gender,
            "address" to profile.address
        )
        firestore.collection("users").document(uid).update(updates)
    }

    fun updateNotificationSetting(key: String, value: Any?) {
        val uid = auth.currentUser?.uid ?: return
        val docRef = firestore.collection("users").document(uid).collection("settings").document("notifications")
        if (value == null) {
            if (key != "alarmToneUri") return
            docRef.set(
                hashMapOf<String, Any>(key to FieldValue.delete()),
                com.google.firebase.firestore.SetOptions.merge()
            )
        } else {
            docRef.set(mapOf(key to value), com.google.firebase.firestore.SetOptions.merge())
        }
    }

    fun updatePrivacySetting(key: String, value: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        val updates = mapOf(key to value)
        firestore.collection("users").document(uid).collection("settings").document("privacy")
            .set(updates, com.google.firebase.firestore.SetOptions.merge())
    }

    fun updateCaregiverSetting(caregiverId: String, key: String, value: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        val updates = mapOf(key to value)
        firestore.collection("users").document(uid).collection("caregivers").document(caregiverId)
            .update(updates)
    }

    fun deleteAccount(onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val user = auth.currentUser ?: return
        val uid = user.uid

        viewModelScope.launch {
            try {
                // Delete user data from collections
                val healthReadingsRef = firestore.collection("health_readings").whereEqualTo("userId", uid).get().await()
                for (doc in healthReadingsRef) {
                    doc.reference.delete().await()
                }

                val remindersRef = firestore.collection("reminders").whereEqualTo("userId", uid).get().await()
                for (doc in remindersRef) {
                    doc.reference.delete().await()
                }
                
                // Delete subcollections sequentially
                firestore.collection("users").document(uid).collection("settings").document("notifications").delete().await()
                firestore.collection("users").document(uid).collection("settings").document("privacy").delete().await()
                
                val caregiversRef = firestore.collection("users").document(uid).collection("caregivers").get().await()
                for (doc in caregiversRef) {
                    doc.reference.delete().await()
                }

                // Delete the main user doc
                firestore.collection("users").document(uid).delete().await()
                
                // Finally delete the auth user
                user.delete().await()
                
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun logout() {
        auth.signOut()
    }
}
