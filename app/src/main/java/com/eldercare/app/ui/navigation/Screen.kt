package com.eldercare.app.ui.navigation

import android.net.Uri

/**
 * Sealed class defining all navigation routes in the app.
 */
sealed class Screen(val route: String) {
    data object RoleSelection : Screen("role_selection")
    data object Login : Screen("login/{role}") {
        fun createRoute(role: String) = "login/$role"
    }
    data object Register : Screen("register/{role}") {
        fun createRoute(role: String) = "register/$role"
    }
    data object ForgotPassword : Screen("forgot_password?email={email}") {
        fun createRoute(email: String) = "forgot_password?email=${Uri.encode(email)}"
    }
    data object ElderlyDashboard : Screen("elderly_dashboard")
    data object CaregiverDashboard : Screen("caregiver_dashboard")
    
    // Settings Flow
    data object Settings : Screen("settings")
    data object CaregiverSettingsMain : Screen("caregiver_settings_main")
    data object ManageProfile : Screen("manage_profile")
    data object NotificationControls : Screen("notification_controls")
    data object CaregiverSettings : Screen("caregiver_settings")
    data object Accessibility : Screen("accessibility")
    data object Security : Screen("security")
    data object ChangePassword : Screen("change_password")
    data object PrivacyControls : Screen("privacy_controls")
    data object AboutHelp : Screen("about_help")
    
    // About/Help Sub-pages
    data object UserGuide : Screen("user_guide")
    data object Faqs : Screen("faqs")
    data object PrivacyPolicy : Screen("privacy_policy")
    data object ContactSupport : Screen("contact_support")
    
    // Dashboard Flow Ext
    data object HealthReadingMedication : Screen("health_reading_medication")
    data object SetAppointment : Screen("set_appointment")
    data object ReadingResultsMonthList : Screen("reading_results_month_list")
    data object NotificationReadingResult : Screen("notification_reading_result/{month}") {
        fun createRoute(month: String) = "notification_reading_result/$month"
    }
    data object NotificationMissedMedication : Screen("notification_missed_medication")
    data object ReminderAlert : Screen("reminder_alert/{id}") {
        fun createRoute(id: String) = "reminder_alert/$id"
    }
}
