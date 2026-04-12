package com.eldercare.app.data.repository

import com.eldercare.app.data.model.CaregiverUser
import com.eldercare.app.data.model.ElderlyUser
import com.eldercare.app.data.model.UserRole
import com.google.firebase.auth.FirebaseUser

/**
 * Interface defining authentication operations for the ElderCare app.
 */
interface AuthRepository {

    /** Returns the currently signed-in Firebase user, or null. */
    val currentUser: FirebaseUser?

    /** Whether a user is currently signed in. */
    val isLoggedIn: Boolean

    /**
     * Signs in a user with email and password.
     * @return [Result] wrapping a [Pair] of [FirebaseUser] and detected [UserRole].
     */
    suspend fun login(email: String, password: String): Result<Pair<FirebaseUser, UserRole>>

    /**
     * Registers a new Elderly user with email/password
     * and creates the corresponding Firestore document.
     */
    suspend fun registerElderly(
        email: String,
        password: String,
        fullName: String,
        age: Int,
        gender: String,
        contactNumber: String
    ): Result<FirebaseUser>

    /**
     * Registers a new Caregiver user with email/password
     * and creates the corresponding Firestore document.
     */
    suspend fun registerCaregiver(
        email: String,
        password: String,
        fullName: String,
        contactNumber: String
    ): Result<FirebaseUser>

    /** Signs the current user out. */
    fun logout()

    /**
     * Sends a password reset email via Firebase Auth.
     * The user will receive a link in their Gmail to reset their password.
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
}
