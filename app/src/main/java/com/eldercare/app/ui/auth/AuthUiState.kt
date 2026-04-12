package com.eldercare.app.ui.auth

import com.eldercare.app.data.model.UserRole

/**
 * Represents the UI state of the authentication screens.
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val userRole: UserRole? = null
)
