package com.eldercare.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyControlsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val privacySettings by viewModel.privacySettings.collectAsState()

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
                    Spacer(modifier = Modifier.width(32.dp))
                    Text(
                        text = "Privacy\nControls",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        lineHeight = 28.sp
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Manage your privacy settings to protect sensitive information.",
                fontSize = 14.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ToggleSettingCard(
                title = "Hide Health Readings on screen",
                subtitle = "Hide health readings on main screen",
                checked = privacySettings.hideHealthReadings,
                onCheckedChange = { viewModel.updatePrivacySetting("hideHealthReadings", it) }
            )

            ToggleSettingCard(
                title = "Hide Notification Previews",
                subtitle = "Show notifications as \"new Alerts\" only",
                checked = privacySettings.hideNotificationPreviews,
                onCheckedChange = { viewModel.updatePrivacySetting("hideNotificationPreviews", it) }
            )

            Text(
                text = "Caregiver Alerts",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
            Text(
                text = "Select alerts to share to your caregiver",
                fontSize = 12.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ToggleSettingCard(
                title = "Share Missed Medication Alerts",
                subtitle = "Send alerts to your caregiver if a medication is missed",
                checked = privacySettings.shareMissedMedication,
                onCheckedChange = { viewModel.updatePrivacySetting("shareMissedMedication", it) }
            )

            ToggleSettingCard(
                title = "Share Abnormal Reading Alerts",
                subtitle = "Send alerts to your caregiver if a health reading is abnormal",
                checked = privacySettings.shareAbnormalReading,
                onCheckedChange = { viewModel.updatePrivacySetting("shareAbnormalReading", it) }
            )
        }
    }
}
