package com.eldercare.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eldercare.app.data.model.UserRole
import com.eldercare.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /** Check if user is already logged in on launch. */
    val isLoggedIn: Boolean
        get() = authRepository.isLoggedIn

    // ── Login ────────────────────────────────────────────────

    fun login(email: String, password: String, expectedRole: UserRole) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill in all fields.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            authRepository.login(email, password)
                .onSuccess { (_, role) ->
                    if (role != expectedRole) {
                        // Role mismatch — sign out and show error
                        authRepository.logout()
                        val roleLabel = if (role == UserRole.ELDERLY) "Elderly" else "Caregiver"
                        val selectedLabel = if (expectedRole == UserRole.ELDERLY) "Elderly" else "Caregiver"
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "This account is registered as \"$roleLabel\". " +
                                    "Please go back and select \"$roleLabel\" to log in, " +
                                    "or use a different $selectedLabel account."
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                userRole = role
                            )
                        }
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.localizedMessage
                                ?: "Login failed. Please try again."
                        )
                    }
                }
        }
    }

    // ── Registration (Elderly) ───────────────────────────────

    fun registerElderly(
        email: String,
        password: String,
        fullName: String,
        age: Int,
        gender: String,
        contactNumber: String
    ) {
        if (email.isBlank() || password.isBlank() || fullName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill in all required fields.") }
            return
        }
        if (password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            authRepository.registerElderly(email, password, fullName, age, gender, contactNumber)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            userRole = UserRole.ELDERLY
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.localizedMessage
                                ?: "Registration failed. Please try again."
                        )
                    }
                }
        }
    }

    // ── Registration (Caregiver) ─────────────────────────────

    fun registerCaregiver(
        email: String,
        password: String,
        fullName: String,
        contactNumber: String
    ) {
        if (email.isBlank() || password.isBlank() || fullName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill in all required fields.") }
            return
        }
        if (password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            authRepository.registerCaregiver(email, password, fullName, contactNumber)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            userRole = UserRole.CAREGIVER
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.localizedMessage
                                ?: "Registration failed. Please try again."
                        )
                    }
                }
        }
    }

    // ── Logout ───────────────────────────────────────────────

    fun logout() {
        authRepository.logout()
        _uiState.update { AuthUiState() }
    }

    /** Clears any error so the user can retry. */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /** Resets success state after navigation has been handled. */
    fun resetState() {
        _uiState.update { AuthUiState() }
    }

    // ── Password Reset ──────────────────────────────────────

    private val _passwordResetState = MutableStateFlow(PasswordResetState())
    val passwordResetState: StateFlow<PasswordResetState> = _passwordResetState.asStateFlow()

    fun sendPasswordResetEmail(email: String) {
        if (email.isBlank()) {
            _passwordResetState.update {
                it.copy(errorMessage = "Please enter your email address.", isSuccess = false)
            }
            return
        }

        viewModelScope.launch {
            _passwordResetState.update { it.copy(isLoading = true, errorMessage = null, isSuccess = false) }

            authRepository.sendPasswordResetEmail(email)
                .onSuccess {
                    _passwordResetState.update {
                        it.copy(isLoading = false, isSuccess = true, sentToEmail = email)
                    }
                }
                .onFailure { exception ->
                    _passwordResetState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.localizedMessage
                                ?: "Failed to send reset email. Please try again."
                        )
                    }
                }
        }
    }

    fun resetPasswordResetState() {
        _passwordResetState.update { PasswordResetState() }
    }
}

data class PasswordResetState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val sentToEmail: String = "",
    val errorMessage: String? = null
)
