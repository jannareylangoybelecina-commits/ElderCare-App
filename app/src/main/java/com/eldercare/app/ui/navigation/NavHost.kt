package com.eldercare.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eldercare.app.data.model.UserRole
import com.eldercare.app.ui.auth.ForgotPasswordScreen
import com.eldercare.app.ui.auth.LoginScreen
import com.eldercare.app.ui.auth.RegisterScreen
import com.eldercare.app.ui.auth.RoleSelectionScreen
import com.eldercare.app.ui.dashboard.CaregiverDashboardScreen
import com.eldercare.app.ui.dashboard.ElderlyDashboardScreen
import com.eldercare.app.ui.dashboard.HealthReadingMedicationScreen
import com.eldercare.app.ui.dashboard.SetAppointmentScreen
import com.eldercare.app.ui.dashboard.ReadingResultsMonthListScreen
import com.eldercare.app.ui.dashboard.NotificationReadingResultScreen
import com.eldercare.app.ui.dashboard.NotificationMissedMedicationScreen
import com.eldercare.app.ui.dashboard.ReminderAlertScreen
import com.eldercare.app.ui.settings.CaregiverSettingsMainScreen
import com.eldercare.app.ui.settings.CaregiverSettingsScreen
import com.eldercare.app.ui.settings.ManageProfileScreen
import com.eldercare.app.ui.settings.NotificationControlsScreen
import com.eldercare.app.ui.settings.SettingsScreen
import com.eldercare.app.ui.settings.AccessibilityScreen
import com.eldercare.app.ui.settings.SecurityScreen
import com.eldercare.app.ui.settings.ChangePasswordScreen
import com.eldercare.app.ui.settings.PrivacyControlsScreen
import com.eldercare.app.ui.settings.AboutHelpScreen
import com.eldercare.app.ui.settings.UserGuideScreen
import com.eldercare.app.ui.settings.FaqsScreen
import com.eldercare.app.ui.settings.PrivacyPolicyScreen
import com.eldercare.app.ui.settings.ContactSupportScreen

@Composable
fun ElderCareNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.RoleSelection.route
    ) {
        // ── Screen 1: Role Selection ─────────────────────────
        composable(route = Screen.RoleSelection.route) {
            RoleSelectionScreen(
                onElderlySelected = {
                    navController.navigate(Screen.Login.createRoute("elderly"))
                },
                onCaregiverSelected = {
                    navController.navigate(Screen.Login.createRoute("caregiver"))
                }
            )
        }

        // ── Screen 2: Login ──────────────────────────────────
        composable(
            route = Screen.Login.route,
            arguments = listOf(
                navArgument("role") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "elderly"

            LoginScreen(
                role = role,
                onLoginSuccess = { userRole ->
                    val destination = when (userRole) {
                        UserRole.ELDERLY -> Screen.ElderlyDashboard.route
                        UserRole.CAREGIVER -> Screen.CaregiverDashboard.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.RoleSelection.route) { inclusive = true }
                    }
                },
                onForgotPassword = { email ->
                    navController.navigate(
                        Screen.ForgotPassword.createRoute(email.ifBlank { "none" })
                    )
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.createRoute(role))
                }
            )
        }

        // ── Screen 3: Forgot Password ────────────────────────
        composable(
            route = Screen.ForgotPassword.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""

            ForgotPasswordScreen(
                email = if (email == "none") "" else email,
                onNavigateBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() }
            )
        }

        // ── Screen 4: Register ───────────────────────────────
        composable(
            route = Screen.Register.route,
            arguments = listOf(
                navArgument("role") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "elderly"

            RegisterScreen(
                role = role,
                onRegistrationSuccess = { role ->
                    val destination = when (role) {
                        UserRole.ELDERLY -> Screen.ElderlyDashboard.route
                        UserRole.CAREGIVER -> Screen.CaregiverDashboard.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.RoleSelection.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ── Elderly Dashboard ────────────────────────────────
        composable(route = Screen.ElderlyDashboard.route) {
            ElderlyDashboardScreen(
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToHealthReadingMedication = { navController.navigate(Screen.HealthReadingMedication.route) },
                onNavigateToSetAppointment = { navController.navigate(Screen.SetAppointment.route) },
                onNavigateToReadingResultsMonthList = { navController.navigate(Screen.ReadingResultsMonthList.route) },
                onNavigateToNotificationMissedMedication = { navController.navigate(Screen.NotificationMissedMedication.route) },
                onNavigateToReminderAlert = { id -> navController.navigate(Screen.ReminderAlert.createRoute(id)) }
            )
        }

        // ── Dashboard Deep Links ──────────────────────────────
        composable(route = Screen.HealthReadingMedication.route) {
            HealthReadingMedicationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(route = Screen.SetAppointment.route) {
            SetAppointmentScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(route = Screen.ReadingResultsMonthList.route) {
            ReadingResultsMonthListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { month -> navController.navigate(Screen.NotificationReadingResult.createRoute(month)) }
            )
        }
        composable(
            route = Screen.NotificationReadingResult.route,
            arguments = listOf(navArgument("month") { type = NavType.StringType })
        ) { backStackEntry ->
            val month = backStackEntry.arguments?.getString("month") ?: "January"
            NotificationReadingResultScreen(
                month = month,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(route = Screen.NotificationMissedMedication.route) {
            NotificationMissedMedicationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(
            route = Screen.ReminderAlert.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
            deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "eldercare://reminder_alert/{id}" })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            ReminderAlertScreen(
                reminderId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Settings Screen ───────────────────────────────────
        composable(route = Screen.Settings.route) {
            val authViewModel: com.eldercare.app.ui.auth.AuthViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            val context = androidx.compose.ui.platform.LocalContext.current
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToManageProfile = { navController.navigate(Screen.ManageProfile.route) },
                onNavigateToNotificationControls = { navController.navigate(Screen.NotificationControls.route) },
                onNavigateToCaregiverSettings = { navController.navigate(Screen.CaregiverSettings.route) },
                onNavigateToAccessibility = { navController.navigate(Screen.Accessibility.route) },
                onNavigateToSecurity = { navController.navigate(Screen.Security.route) },
                onNavigateToAboutHelp = { navController.navigate(Screen.AboutHelp.route) },
                onLogout = {
                    authViewModel.logout()
                    android.widget.Toast.makeText(context, "Logout Successfully", android.widget.Toast.LENGTH_SHORT).show()
                    navController.navigate(Screen.RoleSelection.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── Caregiver Settings Flow ──────────────────────────
        composable(route = Screen.CaregiverSettingsMain.route) {
            CaregiverSettingsMainScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToManageProfile = { navController.navigate(Screen.ManageProfile.route) },
                onNavigateToNotificationControls = { navController.navigate(Screen.NotificationControls.route) },
                onNavigateToAccessibility = { navController.navigate(Screen.Accessibility.route) },
                onNavigateToSecurity = { navController.navigate(Screen.Security.route) },
                onNavigateToAboutHelp = { navController.navigate(Screen.AboutHelp.route) }
            )
        }

        // ── Shared Settings Screens ──────────────────────────
        composable(route = Screen.ManageProfile.route) {
            ManageProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.NotificationControls.route) {
            NotificationControlsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.CaregiverSettings.route) {
            CaregiverSettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Accessibility.route) {
            AccessibilityScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Security.route) {
            SecurityScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToChangePassword = { navController.navigate(Screen.ChangePassword.route) },
                onNavigateToPrivacyControls = { navController.navigate(Screen.PrivacyControls.route) }
            )
        }

        composable(route = Screen.ChangePassword.route) {
            ChangePasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.PrivacyControls.route) {
            PrivacyControlsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.AboutHelp.route) {
            AboutHelpScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToUserGuide = { navController.navigate(Screen.UserGuide.route) },
                onNavigateToFaqs = { navController.navigate(Screen.Faqs.route) },
                onNavigateToPrivacyPolicy = { navController.navigate(Screen.PrivacyPolicy.route) },
                onNavigateToContactSupport = { navController.navigate(Screen.ContactSupport.route) }
            )
        }

        // ── About/Help Sub-pages ─────────────────────────────
        composable(route = Screen.UserGuide.route) {
            UserGuideScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = Screen.Faqs.route) {
            FaqsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = Screen.PrivacyPolicy.route) {
            PrivacyPolicyScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = Screen.ContactSupport.route) {
            ContactSupportScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ── Caregiver Dashboard ──────────────────────────────
        composable(route = Screen.CaregiverDashboard.route) {
            val context = androidx.compose.ui.platform.LocalContext.current
            val authViewModel: com.eldercare.app.ui.auth.AuthViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            CaregiverDashboardScreen(
                onLogout = {
                    authViewModel.logout()
                    android.widget.Toast.makeText(context, "Logout Successfully", android.widget.Toast.LENGTH_SHORT).show()
                    navController.navigate(Screen.RoleSelection.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.CaregiverSettingsMain.route)
                },
                onNavigateToReadingResultsMonthList = {
                    navController.navigate(Screen.ReadingResultsMonthList.route)
                },
                onNavigateToNotificationMissedMedication = {
                    navController.navigate(Screen.NotificationMissedMedication.route)
                }
            )
        }
    }
}
