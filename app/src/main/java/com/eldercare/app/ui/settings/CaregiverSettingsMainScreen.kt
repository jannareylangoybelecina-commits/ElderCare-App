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

/**
 * Caregiver-specific settings screen.
 * Same as elderly SettingsScreen but without "Caregiver Settings" row.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverSettingsMainScreen(
    onNavigateBack: () -> Unit,
    onNavigateToManageProfile: () -> Unit,
    onNavigateToNotificationControls: () -> Unit,
    onNavigateToAccessibility: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    onNavigateToAboutHelp: () -> Unit
) {
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
                        text = "Settings",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsItemRow("Manage Profile", onClick = onNavigateToManageProfile)
            SettingsItemRow("Notification Controls", onClick = onNavigateToNotificationControls)
            SettingsItemRow("Accessibility", onClick = onNavigateToAccessibility)
            SettingsItemRow("Security", onClick = onNavigateToSecurity)
            SettingsItemRow("About / Help", onClick = onNavigateToAboutHelp)
        }
    }
}
