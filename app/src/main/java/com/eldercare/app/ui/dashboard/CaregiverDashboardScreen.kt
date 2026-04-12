package com.eldercare.app.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.eldercare.app.R
import com.eldercare.app.ui.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverDashboardScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    dashboardViewModel: DashboardViewModel = hiltViewModel(),
    onLogout: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToReadingResultsMonthList: () -> Unit = {},
    onNavigateToNotificationMissedMedication: () -> Unit = {}
) {
    val userName by dashboardViewModel.userName.collectAsState()
    var selectedBottomTab by remember { mutableStateOf(0) }
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFE6EFF5),
        bottomBar = {
            CaregiverBottomBar(
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
            when (selectedBottomTab) {
                0 -> {
                    // ── HOME TAB ─────────────────────────────
                    // Top header with kebab menu
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
                                        .clip(androidx.compose.foundation.shape.CircleShape)
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

                            Box {
                                IconButton(onClick = { showMenu = true }) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Menu",
                                        tint = Color(0xFF1E293B),
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false },
                                    offset = DpOffset(0.dp, 0.dp)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Settings") },
                                        onClick = {
                                            showMenu = false
                                            onNavigateToSettings()
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.Settings, contentDescription = null)
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Logout", color = Color(0xFFD32F2F)) },
                                        onClick = {
                                            showMenu = false
                                            onLogout()
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Logout,
                                                contentDescription = null,
                                                tint = Color(0xFFD32F2F)
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }

                    val healthReadings by dashboardViewModel.healthReadings.collectAsState()
                    val reminders by dashboardViewModel.reminders.collectAsState()

                    // Home content — Show Elderly Pending Medications & Health Readings
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp, top = 8.dp)
                    ) {
                        // 1) Pending Reminders (Appointments)
                        item {
                            Text(
                                text = "Pending Reminders",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        val pendingAppointments = reminders.filter { !it.isMedication && !it.isCompleted }
                        if (pendingAppointments.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Text("No pending reminders.", color = Color.Gray, fontSize = 16.sp)
                                }
                            }
                        } else {
                            items(pendingAppointments.size) { index ->
                                val reminder = pendingAppointments[index]
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .background(Color(0xFFECA39E), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = Color.Black)
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text("${reminder.title} - ${reminder.timeString}", fontSize = 15.sp, color = Color.Black)
                                }
                            }
                        }

                        // 2) Pending Medication Tracker
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Pending Medication Tracker",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        val pendingMeds = reminders.filter { it.isMedication && !it.isCompleted }
                        if (pendingMeds.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Text("No pending medications.", color = Color.Gray, fontSize = 16.sp)
                                }
                            }
                        } else {
                            items(pendingMeds.size) { index ->
                                val med = pendingMeds[index]
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .background(Color(0xFFECA39E), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Cancel, contentDescription = null, tint = Color.Black)
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text("${med.title} - ${med.timeString}", fontSize = 15.sp, color = Color.Black)
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Elderly Health Readings",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        if (healthReadings.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Text("No Health Readings found yet.", color = Color.Gray, fontSize = 16.sp)
                                }
                            }
                        } else {
                            items(healthReadings.size) { index ->
                                val reading = healthReadings[index]
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = reading.date,
                                                fontSize = 14.sp,
                                                color = Color.Gray
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "BP: ${reading.systolic}/${reading.diastolic} mmHg",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF2B7EC1)
                                            )
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("HR: ${reading.heartRate} bpm", fontSize = 14.sp, color = Color.Black)
                                            Text("W: ${reading.weight} kg", fontSize = 14.sp, color = Color.Black)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // ── NOTIFICATION TAB ─────────────────────
                    // Top header with logo
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE6EFF5))
                            .statusBarsPadding()
                            .padding(top = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.eldercare_logo),
                            contentDescription = "ElderCare Logo",
                            modifier = Modifier.size(80.dp),
                            contentScale = ContentScale.Fit
                        )

                        Text(
                            text = "ElderCare",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2B7EC1)
                        )

                        Text(
                            text = "Self Health Monitoring App",
                            fontSize = 11.sp,
                            color = Color(0xFF7A8A99)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Notification content
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
                    ) {
                        item {
                            Text(
                                text = "Notifications",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToReadingResultsMonthList() },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 28.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Reading Results",
                                        fontSize = 20.sp,
                                        color = Color.Black
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = Color.Black
                                    )
                                }
                            }
                        }
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToNotificationMissedMedication() },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 28.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Missed Medications",
                                        fontSize = 20.sp,
                                        color = Color.Black
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CaregiverBottomBar(selectedItem: Int, onItemSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple("Home", Icons.Default.Home, 0),
            Triple("Notification", Icons.Default.Notifications, 1)
        )
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(item.second, contentDescription = item.first)
                },
                label = {
                    Text(
                        item.first,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
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
