package com.eldercare.app.data.repository

import com.eldercare.app.data.model.CaregiverUser
import com.eldercare.app.data.model.ElderlyUser
import com.eldercare.app.data.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    companion object {
        const val USERS_COLLECTION = "users"
    }

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override val isLoggedIn: Boolean
        get() = firebaseAuth.currentUser != null

    override suspend fun login(
        email: String,
        password: String
    ): Result<Pair<FirebaseUser, UserRole>> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
                ?: return Result.failure(Exception("Authentication failed. No user returned."))

            // Determine role by checking the "users" collection
            val userDoc = firestore.collection(USERS_COLLECTION)
                .document(user.uid)
                .get()
                .await()
                
            val role = if (userDoc.exists()) {
                val roleString = userDoc.getString("role")
                if (roleString == "caregiver") UserRole.CAREGIVER else UserRole.ELDERLY
            } else {
                return Result.failure(Exception("User profile not found. Please register first."))
            }

            Result.success(Pair(user, role))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registerElderly(
        email: String,
        password: String,
        fullName: String,
        age: Int,
        gender: String,
        contactNumber: String
    ): Result<FirebaseUser> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
                ?: return Result.failure(Exception("Registration failed. No user returned."))

            val userData = mapOf(
                "elderly_id" to user.uid,
                "fullName" to fullName,
                "age" to age,
                "gender" to gender,
                "phone" to contactNumber,
                "email" to email,
                "role" to "elderly"
            )

            firestore.collection(USERS_COLLECTION)
                .document(user.uid)
                .set(userData)
                .await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registerCaregiver(
        email: String,
        password: String,
        fullName: String,
        contactNumber: String
    ): Result<FirebaseUser> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
                ?: return Result.failure(Exception("Registration failed. No user returned."))

            val userData = mapOf(
                "caregiver_id" to user.uid,
                "fullName" to fullName,
                "phone" to contactNumber,
                "email" to email,
                "role" to "caregiver"
            )

            firestore.collection(USERS_COLLECTION)
                .document(user.uid)
                .set(userData)
                .await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
