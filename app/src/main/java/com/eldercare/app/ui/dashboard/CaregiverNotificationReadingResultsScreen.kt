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
import com.eldercare.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverNotificationReadingUserListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetails: (String) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val notificationReadings by viewModel.notificationHealthReadings.collectAsState()
    val elderlyUsersMap by viewModel.elderlyUsersMap.collectAsState()
    
    // Extract unique elderly users that have pending notifications
    val userIdsWithNotifications = notificationReadings.map { it.userId }.distinct()

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
                        text = "Reading Results",
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
            if (userIdsWithNotifications.isEmpty()) {
                item {
                    Text(
                        text = "No recent reading results.",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(userIdsWithNotifications) { userId ->
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
fun CaregiverNotificationReadingDetailsScreen(
    targetUserId: String,
    onNavigateBack: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val notificationReadings by viewModel.notificationHealthReadings.collectAsState()
    val elderlyUsersMap by viewModel.elderlyUsersMap.collectAsState()
    
    val userName = elderlyUsersMap[targetUserId] ?: "Elderly User"
    
    val userRecords = notificationReadings
        .filter { it.userId == targetUserId }
        .sortedBy { getEpochTime(it.date) }

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
                        text = "Reading Results",
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
                        text = "No recent reading results.",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(userRecords) { reading ->
                    HistoryReadingItemCard(reading)
                }
            }
        }
    }
}

// Reusable card extracted for chronological Notification / History layout
@Composable
fun HistoryReadingItemCard(reading: HealthReadingItem) {
    val sys = reading.systolic.toIntOrNull() ?: 0
    val dia = reading.diastolic.toIntOrNull() ?: 0
    val isAbnormal = !(sys in 1..120 && dia in 1..80)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Box containing Date and Status directly together based on previous structural requirements
            // Box containing Date and Status directly together, dynamically colored by status
            val headerBgColor = if (isAbnormal) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerBgColor, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = reading.date, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(
                        text = if (isAbnormal) "ABNORMAL" else "NORMAL",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAbnormal) Color(0xFFD32F2F) else Color(0xFF388E3C)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Values
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BP: ${reading.systolic}/${reading.diastolic} mmHg",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text("HR: ${reading.heartRate} bpm", fontSize = 14.sp, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("W: ${reading.weight} kg", fontSize = 14.sp, color = Color.Black)
                }
            }
        }
    }
}

private fun getEpochTime(dateStr: String): Long {
    try {
        val formats = listOf("MMMM d, yyyy", "MM/dd/yyyy", "yyyy-MM-dd")
        val formatter = java.text.SimpleDateFormat()
        for (fmt in formats) {
            try {
                formatter.applyPattern(fmt)
                val d = formatter.parse(dateStr)
                if (d != null) {
                    return d.time
                }
            } catch (e: Exception) {}
        }
    } catch(e: Exception){}
    return 0L
}
