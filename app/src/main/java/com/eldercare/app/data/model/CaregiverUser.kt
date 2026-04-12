package com.eldercare.app.data.model

/**
 * Represents a Caregiver user in the system.
 * Maps to the "Caregiver_User" collection in Firestore.
 */
data class CaregiverUser(
    val caregiver_id: String = "",
    val full_name: String = "",
    val contact_number: String = "",
    val email: String = "",
    val assigned_elderly_ids: List<String> = emptyList()
)
