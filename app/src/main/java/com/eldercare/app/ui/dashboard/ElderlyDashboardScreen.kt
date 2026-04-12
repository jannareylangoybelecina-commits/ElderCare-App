package com.eldercare.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElderlyDashboardScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToHealthReadingMedication: () -> Unit,
    onNavigateToSetAppointment: () -> Unit,
    onNavigateToReadingResultsMonthList: () -> Unit,
    onNavigateToNotificationMissedMedication: () -> Unit,
    onNavigateToReminderAlert: (String) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val userName by viewModel.userName.collectAsState()
    val reminders by viewModel.reminders.collectAsState()
    val healthReadings by viewModel.healthReadings.collectAsState()

    var selectedBottomTab by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = Color(0xFFE6EFF5),
        bottomBar = {
            ElderlyBottomBar(
                selectedItem = selectedBottomTab,
                onItemSelected = { selectedBottomTab = it }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Section
            TopHeader(userName = userName, onNavigateToSettings = onNavigateToSettings)

            // Content based on Bottom Tab
            // (For now, only Home is fully implemented as shown in screenshots)
            when (selectedBottomTab) {
                0 -> {
                    // Home content
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(8.dp)) }
                        item {
                            val generalReminders = reminders.filter { !it.isMedication }
                            DashboardSection(title = "Reminders") {
                                if (generalReminders.isEmpty()) {
                                    Text("No upcoming reminders", color = Color.Gray, modifier = Modifier.padding(16.dp))
                                } else {
                                    generalReminders.forEach { reminder ->
                                        ReminderCard(
                                            icon = if (reminder.title.contains("Check", true)) Icons.Default.CalendarMonth else Icons.Default.Schedule,
                                            title = reminder.title,
                                            time = reminder.timeString,
                                            isCompleted = reminder.isCompleted,
                                            isActionable = false,
                                            onClick = { onNavigateToReminderAlert(reminder.id) },
                                            onDelete = { viewModel.deleteReminder(reminder.id) }
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            val medReminders = reminders.filter { it.isMedication }
                            DashboardSection(title = "Medication Tracker") {
                                if (medReminders.isEmpty()) {
                                    Text("No medications scheduled", color = Color.Gray, modifier = Modifier.padding(16.dp))
                                } else {
                                    medReminders.forEach { reminder ->
                                        ReminderCard(
                                            icon = if (reminder.isCompleted) Icons.Default.CheckCircleOutline else Icons.Default.Cancel,
                                            title = reminder.title,
                                            time = reminder.timeString,
                                            isCompleted = reminder.isCompleted,
                                            isActionable = true,
                                            onClick = { onNavigateToReminderAlert(reminder.id) },
                                            onDelete = { viewModel.deleteReminder(reminder.id) }
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            DashboardSection(title = "Health Readings") {
                                if (healthReadings.isEmpty()) {
                                    Text("No health readings recorded yet", color = Color.Gray, modifier = Modifier.padding(16.dp))
                                } else {
                                    healthReadings.forEach { reading ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 6.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color(0xFFEEF5FD))
                                                .padding(horizontal = 16.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(reading.date, fontSize = 14.sp, color = Color.Gray)
                                                Text("BP: ${reading.systolic}/${reading.diastolic} mmHg", fontWeight = FontWeight.Bold, color = Color(0xFF2B7EC1))
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("HR: ${reading.heartRate} bpm", fontSize = 14.sp, color = Color.Black)
                                                Text("W: ${reading.weight} kg", fontSize = 14.sp, color = Color.Black)
                                            }
                                            IconButton(onClick = { viewModel.deleteHealthReading(reading.id) }) {
                                                Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete", tint = Color.Red)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Set Reminder / Appointment / Medication
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
                    ) {
                        item {
                            Text("Set Reminder", fontSize = 28.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { onNavigateToHealthReadingMedication() },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("- Health Reading Results", fontSize = 16.sp, color = Color.Black)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("- Set Medication", fontSize = 16.sp, color = Color.Black)
                                    }
                                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.Black)
                                }
                            }
                        }
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { onNavigateToSetAppointment() },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.AddCircleOutline, contentDescription = null, tint = Color.Black, modifier = Modifier.size(28.dp))
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text("Set Appointment", fontSize = 16.sp, color = Color.Black)
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Notifications
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
                    ) {
                        item {
                            Text("Notifications", fontSize = 28.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { onNavigateToReadingResultsMonthList() },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 28.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Reading Results", fontSize = 20.sp, color = Color.Black)
                                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.Black)
                                }
                            }
                        }
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { onNavigateToNotificationMissedMedication() },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 28.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Missed Medications", fontSize = 20.sp, color = Color.Black)
                                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.Black)
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // Health History
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
                    ) {
                        item {
                            Text("Health History", fontSize = 28.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { onNavigateToReadingResultsMonthList() },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 28.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Reading Results", fontSize = 20.sp, color = Color.Black)
                                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.Black)
                                }
                            }
                        }
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { onNavigateToNotificationMissedMedication() },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 28.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Missed Medications", fontSize = 20.sp, color = Color.Black)
                                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.Black)
                                }
                            }
                        }
                    }
                }
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Feature coming soon", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun TopHeader(userName: String, onNavigateToSettings: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
            .background(Color(0xFFB3D1E6))
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonOutline,
                        contentDescription = "Profile",
                        tint = Color.DarkGray,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Welcome, $userName!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1E293B)
                )
            }

            IconButton(onClick = onNavigateToSettings) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Settings",
                    tint = Color(0xFF1E293B),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun DashboardSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun ReminderCard(
    icon: ImageVector,
    title: String,
    time: String,
    isCompleted: Boolean,
    isActionable: Boolean,
    onClick: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val backgroundColor = if (!isCompleted) Color(0xFFECA39E) else Color(0xFFC7F0C8)
    
    // Fallback UI mapping
    val displayIcon = icon

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = displayIcon,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "$title - $time",
            fontSize = 15.sp,
            color = Color.Black,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onDelete) {
            Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete", tint = Color.Black)
        }
    }
}

@Composable
fun ElderlyBottomBar(selectedItem: Int, onItemSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple("Home", Icons.Default.Home, 0),
            Triple("Set Reminder\nAppointment/\nMedication", Icons.Default.MedicalServices, 1),
            Triple("Notification", Icons.Default.Notifications, 2),
            Triple("Health\nHistory", Icons.Default.Assignment, 3)
        )
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Box {
                        Icon(item.second, contentDescription = item.first)
                        // Add badge to notification
                        if (index == 2) {
                            Badge(
                                modifier = Modifier.align(Alignment.TopEnd).offset(x=8.dp, y=(-4).dp)
                            ) { Text("2") }
                        }
                    }
                },
                label = {
                    Text(
                        item.first,
                        fontSize = 10.sp,
                        lineHeight = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                },
                selected = selectedItem == index,
                onClick = { onItemSelected(index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF6B8A9E),
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Color(0xFF6B8A9E),
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
