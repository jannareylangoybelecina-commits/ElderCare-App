package com.eldercare.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationMissedMedicationScreen(
    onNavigateBack: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val todayMissed by viewModel.todayMissedMedications.collectAsState()
    val elderlyUsersMap by viewModel.elderlyUsersMap.collectAsState()
    val userRole by viewModel.userRole.collectAsState()
    val isCaregiver = userRole == "caregiver"
    val caregiverUid = remember { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }
    var pendingDeleteReminderId by remember { mutableStateOf<String?>(null) }
    val visibleMissed = remember(todayMissed, caregiverUid, isCaregiver) {
        if (!isCaregiver || caregiverUid.isBlank()) {
            todayMissed
        } else {
            todayMissed.filter { !it.dismissedByCaregiverIds.contains(caregiverUid) }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                    .statusBarsPadding()
                    .padding(vertical = 16.dp, horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = "Back",
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Missed Medications",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (visibleMissed.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No missed medications today.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = if (isCaregiver) "Today's missed medications (assigned per elderly user)." else "These are your missed medications for today.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(visibleMissed, key = { it.id }) { med ->
                    val elderlyLabel = if (isCaregiver) {
                        elderlyUsersMap[med.userId]?.let { "Elderly: $it" }
                            ?: "Elderly ID: ${med.userId.ifBlank { "—" }}"
                    } else {
                        null
                    }
                    MissedMedicationCard(
                        elderlyLabel = elderlyLabel,
                        med = med,
                        showDeleteAction = isCaregiver,
                        onDeleteClick = { pendingDeleteReminderId = med.id }
                    )
                }
            }
        }
    }

    if (pendingDeleteReminderId != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteReminderId = null },
            title = { Text("Remove notification?") },
            text = { Text("Are you sure you want to remove this notification?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.dismissMissedMedicationNotification(pendingDeleteReminderId.orEmpty())
                        pendingDeleteReminderId = null
                    }
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteReminderId = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun MissedMedicationCard(
    elderlyLabel: String?,
    med: ReminderItem,
    showDeleteAction: Boolean,
    onDeleteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(androidx.compose.ui.graphics.Color(0xFFFFEBEE), RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        if (showDeleteAction) {
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove notification",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            if (elderlyLabel != null) {
                Text("For", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(elderlyLabel, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))
            }
            Text("Medication Name:", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(med.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

            Spacer(modifier = Modifier.height(12.dp))

            Text("Date:", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                med.date.ifBlank { "—" },
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Time:", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(med.timeString, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Missed Dose !",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "This medication was not confirmed as taken. We will log this on your records.",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
