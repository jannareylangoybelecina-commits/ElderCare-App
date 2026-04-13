package com.eldercare.app.ui.theme

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Global dark/light preference wired to [ElderCareTheme] and persisted across process restarts.
 */
object ThemeManager {
    private const val PREFS_NAME = "eldercare_theme"
    private const val KEY_DARK = "is_dark_theme"

    private lateinit var appContext: Context

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme = _isDarkTheme.asStateFlow()

    fun init(context: Context) {
        appContext = context.applicationContext
        val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _isDarkTheme.value = prefs.getBoolean(KEY_DARK, false)
    }

    private fun persist(value: Boolean) {
        if (::appContext.isInitialized) {
            appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_DARK, value)
                .apply()
        }
    }

    fun setDarkTheme(enabled: Boolean) {
        if (_isDarkTheme.value == enabled) return
        _isDarkTheme.value = enabled
        persist(enabled)
    }

    fun toggleTheme() {
        setDarkTheme(!_isDarkTheme.value)
    }
}
