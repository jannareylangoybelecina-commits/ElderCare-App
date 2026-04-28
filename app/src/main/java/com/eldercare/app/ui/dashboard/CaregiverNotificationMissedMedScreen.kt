package com.eldercare.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverNotificationMissedMedUserListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetails: (String) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val reminders by viewModel.reminders.collectAsState()
    val elderlyUsersMap by viewModel.elderlyUsersMap.collectAsState()
    val caregiverUid = remember { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }

    val groupedMissedByUser = reminders
        .asSequence()
        .filter { it.isMedication && it.medicationStatus == DashboardViewModel.MED_STATUS_MISSED }
        .filter { caregiverUid.isBlank() || !it.dismissedByCaregiverIds.contains(caregiverUid) }
        .filter { it.userId.isNotBlank() }
        .groupBy { it.userId }

    val userIdsWithMissedMeds = groupedMissedByUser.entries
        .sortedByDescending { (_, meds) -> meds.maxOfOrNull { parseDateToEpoch(it.date) } ?: 0L }
        .map { it.key }

    Scaffold(
        containerColor = Color(0xFFF3F4F6),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(vertical = 16.dp, horizontal = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Missed Medications",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (userIdsWithMissedMeds.isEmpty()) {
                item {
                    Text(
                        text = "No missed medication records available.",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(userIdsWithMissedMeds) { userId ->
                    val userName = elderlyUsersMap[userId] ?: "Elderly User"
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToDetails(userId) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EEF5)) // Light blue container
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 28.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = userName, fontSize = 20.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverNotificationMissedMedDetailsScreen(
    targetUserId: String,
    onNavigateBack: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val reminders by viewModel.reminders.collectAsState()
    val elderlyUsersMap by viewModel.elderlyUsersMap.collectAsState()
    val caregiverUid = remember { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }
    var pendingDeleteReminderId by remember { mutableStateOf<String?>(null) }
    
    val userName = elderlyUsersMap[targetUserId] ?: "Elderly User"
    
    val userRecords = reminders
        .filter { it.userId == targetUserId }
        .filter { it.isMedication && it.medicationStatus == DashboardViewModel.MED_STATUS_MISSED }
        .filter { caregiverUid.isBlank() || !it.dismissedByCaregiverIds.contains(caregiverUid) }
        .sortedByDescending { parseDateToEpoch(it.date) }

    Scaffold(
        containerColor = Color(0xFFF3F4F6),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(vertical = 16.dp, horizontal = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Missed Medications",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = userName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Left
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (userRecords.isEmpty()) {
                item {
                    Text(
                        text = "No missed medication records.",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(userRecords) { med ->
                    NotificationMissedMedItemCard(
                        med = med,
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

// Reusable card extracted for chronological Missed Meds
@Composable
fun NotificationMissedMedItemCard(
    med: ReminderItem,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)) // Red boundary
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove notification",
                    tint = Color(0xFFC62828)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Left aligned content for details
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                    Text(
                        text = formatToMonthDayYear(med.date),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Medication:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(text = med.title, fontSize = 16.sp, color = Color.Black)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Schedule Time:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(text = med.timeString, fontSize = 16.sp, color = Color.Black)

                    if (med.dosage.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Dosage:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(text = med.dosage, fontSize = 16.sp, color = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color.Black.copy(alpha = 0.2f), modifier = Modifier.padding(bottom = 16.dp))

                // Centered explicit instructions
                Text(
                    text = "Missed Dose!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFC62828), // Dark red emphasis
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Please check in with your patients and identify any scheduled medications that have not yet been taken.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

private fun parseDateToEpoch(dateStr: String): Long {
    val raw = dateStr.trim()
    if (raw.isBlank()) return 0L
    val formats = listOf("MMMM d, yyyy", "MM/dd/yyyy", "yyyy-MM-dd")
    for (pattern in formats) {
        val formatter = SimpleDateFormat(pattern, Locale.US).apply { isLenient = false }
        val parsed = runCatching { formatter.parse(raw) }.getOrNull()
        if (parsed != null) return parsed.time
    }
    return 0L
}

private fun formatToMonthDayYear(dateStr: String): String {
    val epoch = parseDateToEpoch(dateStr)
    if (epoch == 0L) return dateStr.ifBlank { "Unknown date" }
    return SimpleDateFormat("MM/dd/yyyy", Locale.US).format(Date(epoch))
}
