package com.eldercare.app.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.eldercare.app.ui.theme.ThemeManager
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.activity.compose.BackHandler
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverDashboardScreen(
    dashboardViewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit = {},
    onNavigateToReadingResultsMonthList: () -> Unit = {},
    onNavigateToNotificationMissedMedication: () -> Unit = {}
) {
    val userName by dashboardViewModel.userName.collectAsState()
    val elderlyUsersMap by dashboardViewModel.elderlyUsersMap.collectAsState()
    
    var selectedBottomTab by rememberSaveable { mutableStateOf(0) }
    var showMenu by remember { mutableStateOf(false) }
    val isDarkTheme by ThemeManager.isDarkTheme.collectAsState()

    BackHandler(enabled = selectedBottomTab != 0) {
        selectedBottomTab = 0
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
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
                    // Top header sharing identical design alignment with Elderly Home UI
                    TopHeader(
                        userName = userName,
                        onNavigateToSettings = onNavigateToSettings,
                        missedMedicationCount = 0, // Caregiver side does not require personal missed meds alert
                        useMockupLightChrome = !isDarkTheme
                    )

                    val healthReadings by dashboardViewModel.healthReadings.collectAsState()
                    val reminders by dashboardViewModel.reminders.collectAsState()

                    // Home content — Show Elderly Pending Medications & Health Readings ONLY (No Pending Appointments format)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp, top = 24.dp)
                    ) {
                        
                        // 1) Pending Medication Reminder
                        item {
                            Text(
                                text = "Pending Medication Reminder",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        val pendingMeds = reminders.filter {
                            it.isMedication && it.medicationStatus == DashboardViewModel.MED_STATUS_PENDING
                        }
                        if (pendingMeds.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp)
                                        .background(Color(0xFFE8EEF5), RoundedCornerShape(8.dp))
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No pending medications.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 16.sp)
                                }
                            }
                        } else {
                            items(pendingMeds.size) { index ->
                                val med = pendingMeds[index]
                                val userFullName = elderlyUsersMap[med.userId] ?: "Elderly User"
                                
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .background(Color(0xFFFFF59D), RoundedCornerShape(12.dp)) // Yellow background for pending
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(userFullName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Cancel, contentDescription = null, tint = Color.Black)
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text("${med.title} - ${med.timeString}", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
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
                            .background(MaterialTheme.colorScheme.background)
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
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "Self Health Monitoring App",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToReadingResultsMonthList() },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface
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
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface
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
        containerColor = MaterialTheme.colorScheme.surface,
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
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
