package com.eldercare.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
fun CaregiverSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val caregiver by viewModel.caregiverInfo.collectAsState()

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
                        text = "Caregiver\nSettings",
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Manage caregiver connection",
                fontSize = 14.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val name = caregiver?.name ?: "No Caregiver Linked"
            val phone = caregiver?.phone ?: ""

            CaregiverInfoCard(icon = Icons.Default.PersonOutline, text = name, textColor = Color.Black)
            if (phone.isNotEmpty()) {
                CaregiverInfoCard(icon = Icons.Default.Phone, text = phone, textColor = Color.Black)
            }

            CaregiverActionCard(
                icon = Icons.Default.Edit,
                text = "Edit Caregiver",
                textColor = Color(0xFF5BA4E5),
                onClick = {}
            )

            CaregiverActionCard(
                icon = Icons.Default.DeleteOutline,
                text = "Remove Caregiver",
                textColor = Color(0xFFECA39E),
                onClick = {}
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (caregiver != null) {
                ToggleSettingCard(
                    title = "Receive missed\nmedication alerts",
                    checked = caregiver!!.missedMedsAlert,
                    onCheckedChange = { viewModel.updateCaregiverSetting(caregiver!!.id, "receiveMissedMedsAlerts", it) }
                )

                ToggleSettingCard(
                    title = "Receive abnormal\nreading alerts",
                    checked = caregiver!!.abnormalReadingsAlert,
                    onCheckedChange = { viewModel.updateCaregiverSetting(caregiver!!.id, "receiveAbnormalReadingAlerts", it) }
                )
            }
        }
    }
}

@Composable
fun CaregiverInfoCard(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, textColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = text,
                fontSize = 20.sp,
                color = textColor,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
fun CaregiverActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    textColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = text,
                fontSize = 18.sp,
                color = textColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
