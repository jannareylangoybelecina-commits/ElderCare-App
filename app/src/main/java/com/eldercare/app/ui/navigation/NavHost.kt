package com.eldercare.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.eldercare.app.ui.auth.ForgotPasswordScreen
import com.eldercare.app.ui.auth.LoginScreen
import com.eldercare.app.ui.auth.RegisterScreen
import com.eldercare.app.ui.auth.RoleSelectionScreen
import com.eldercare.app.ui.dashboard.*
import com.eldercare.app.ui.settings.*

private fun NavHostController.navigateSingleTop(route: String) {
    navigate(route) {
        launchSingleTop = true
    }
}

@Composable
fun ElderCareNavHost(
    navController: NavHostController,
    startDestination: String = Screen.RoleSelection.route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        
        composable(Screen.RoleSelection.route) {
            RoleSelectionScreen(
                onElderlySelected = { navController.navigate(Screen.Login.createRoute("elderly")) },
                onCaregiverSelected = { navController.navigate(Screen.Login.createRoute("caregiver")) }
            )
        }

        composable(Screen.Login.route) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "elderly"
            LoginScreen(
                role = role,
                onForgotPassword = { email -> navController.navigate(Screen.ForgotPassword.createRoute(email)) },
                onNavigateToRegister = { navController.navigate(Screen.Register.createRoute(role)) },
                onLoginSuccess = { r -> 
                    if (r == com.eldercare.app.data.model.UserRole.ELDERLY) {
                        navController.navigate(Screen.ElderlyDashboard.route) {
                            popUpTo(Screen.RoleSelection.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.CaregiverDashboard.route) {
                            popUpTo(Screen.RoleSelection.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Register.route) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "elderly"
            RegisterScreen(
                role = role,
                onNavigateBack = { navController.navigateUp() },
                onRegistrationSuccess = { r ->
                    if (r == com.eldercare.app.data.model.UserRole.ELDERLY) {
                        navController.navigate(Screen.ElderlyDashboard.route) {
                            popUpTo(Screen.RoleSelection.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.CaregiverDashboard.route) {
                            popUpTo(Screen.RoleSelection.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(
            route = Screen.ForgotPassword.route,
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val emailArg = backStackEntry.arguments?.getString("email").orEmpty()
            ForgotPasswordScreen(
                email = emailArg,
                onNavigateBack = { navController.navigateUp() },
                onSave = { navController.navigateUp() }
            )
        }

        composable(Screen.ElderlyDashboard.route) {
            ElderlyDashboardScreen(
                onNavigateToSettings = { navController.navigateSingleTop(Screen.Settings.route) },
                onNavigateToHealthReadingMedication = { navController.navigateSingleTop(Screen.HealthReadingMedication.route) },
                onNavigateToSetAppointment = { navController.navigateSingleTop(Screen.SetAppointment.route) },
                onNavigateToReadingResultsMonthList = { navController.navigateSingleTop(Screen.ReadingResultsMonthList.route) },
                onNavigateToNotificationMissedMedication = { navController.navigateSingleTop(Screen.NotificationMissedMedication.route) },
                onNavigateToReminderAlert = { id -> navController.navigateSingleTop(Screen.ReminderAlert.createRoute(id)) }
            )
        }

        composable(Screen.CaregiverDashboard.route) {
            CaregiverDashboardScreen(
                onNavigateToSettings = { navController.navigateSingleTop(Screen.Settings.route) },
                onNavigateToReadingResultsMonthList = { navController.navigateSingleTop(Screen.ReadingResultsMonthList.route) },
                onNavigateToNotificationMissedMedication = { navController.navigateSingleTop(Screen.NotificationMissedMedication.route) }
            )
        }

        composable(Screen.HealthReadingMedication.route) {
            HealthReadingMedicationScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.SetAppointment.route) {
            SetAppointmentScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.ReadingResultsMonthList.route) {
            ReadingResultsMonthListScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToDetail = { id -> navController.navigate(Screen.NotificationReadingResult.createRoute(id)) }
            )
        }

        composable(Screen.NotificationReadingResult.route) { backStackEntry ->
            val monthOrId = backStackEntry.arguments?.getString("month") ?: ""
            NotificationReadingResultScreen(
                month = monthOrId,
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.NotificationMissedMedication.route) {
            NotificationMissedMedicationScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.ReminderAlert.route) { backStackEntry ->
            val reminderId = backStackEntry.arguments?.getString("reminderId") ?: ""
            ReminderAlertScreen(
                reminderId = reminderId,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToManageProfile = { navController.navigateSingleTop(Screen.ManageProfile.route) },
                onNavigateToNotificationControls = { navController.navigateSingleTop(Screen.NotificationControls.route) },
                onNavigateToCaregiverSettings = { navController.navigateSingleTop(Screen.CaregiverSettings.route) },
                onNavigateToAccessibility = { navController.navigateSingleTop(Screen.Accessibility.route) },
                onNavigateToSecurity = { navController.navigateSingleTop(Screen.Security.route) },
                onNavigateToAboutHelp = { navController.navigateSingleTop(Screen.AboutHelp.route) },
                onLogout = {
                    navController.navigate(Screen.RoleSelection.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onDeleteAccount = {
                    navController.navigate(Screen.RoleSelection.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.ManageProfile.route) {
            ManageProfileScreen(
                onNavigateBack = { navController.navigateUp() },
                onLogout = {
                    navController.navigate(Screen.RoleSelection.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.NotificationControls.route) {
            NotificationControlsScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        composable(Screen.CaregiverSettings.route) {
            CaregiverSettingsScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(Screen.Accessibility.route) {
            // Placeholder
        }

        composable(Screen.Security.route) {
            SecurityScreen(onNavigateBack = { navController.navigateUp() })
        }

        composable(Screen.AboutHelp.route) {
            AboutHelpScreen(onNavigateBack = { navController.navigateUp() })
        }
    }
}
