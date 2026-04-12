package com.eldercare.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserGuideScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEEF5FD))
                    .statusBarsPadding()
                    .padding(vertical = 16.dp, horizontal = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "User Guide",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            GuideSection(
                title = "Getting Started",
                content = "Welcome to ElderCare! This app helps elderly users and their caregivers " +
                        "manage health readings, medications, and appointments in real time."
            )
            GuideSection(
                title = "Home Screen",
                content = "The Home screen displays your personalized dashboard. For elderly users, " +
                        "it shows reminders, medication trackers, and quick actions. " +
                        "For caregivers, it provides a welcome overview and notification access."
            )
            GuideSection(
                title = "Health Readings",
                content = "Navigate to 'Set Reminder' > 'Health Reading Results' to log your blood " +
                        "pressure (systolic/diastolic), weight, heart rate, and date. These readings " +
                        "are saved in real time and visible to your assigned caregiver."
            )
            GuideSection(
                title = "Medication Tracker",
                content = "Set up your medications with name, dosage, and scheduled time. " +
                        "The app will track whether medications have been taken and alert " +
                        "caregivers about missed doses."
            )
            GuideSection(
                title = "Appointments",
                content = "Use the 'Set Appointment' feature to schedule and manage " +
                        "upcoming doctor visits and check-ups. Appointments appear " +
                        "in your reminders section."
            )
            GuideSection(
                title = "Notifications",
                content = "View reading results and missed medication alerts in the " +
                        "Notifications tab. Caregivers can monitor their patient's " +
                        "health data from this section."
            )
            GuideSection(
                title = "Settings",
                content = "Manage your profile, notification preferences, accessibility " +
                        "options (theme, font size, language), security settings, and " +
                        "view help resources."
            )
        }
    }
}

@Composable
private fun GuideSection(title: String, content: String) {
    Column {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A3A5C)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            fontSize = 15.sp,
            color = Color(0xFF4A4A4A),
            lineHeight = 22.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color(0xFFE0E0E0))
    }
}
