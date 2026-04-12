package com.eldercare.app.data.model

/**
 * Represents an Elderly user in the system.
 * Maps to the "Elderly_User" collection in Firestore.
 */
data class ElderlyUser(
    val elderly_id: String = "",
    val full_name: String = "",
    val age: Int = 0,
    val gender: String = "",
    val contact_number: String = "",
    val email: String = ""
)
