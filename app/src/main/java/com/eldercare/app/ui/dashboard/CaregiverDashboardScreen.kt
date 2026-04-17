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
                    // Top header with kebab menu
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f))
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
                                        .background(MaterialTheme.colorScheme.surface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PersonOutline,
                                        contentDescription = "Profile",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Welcome, $userName!",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Box {
                                IconButton(onClick = { showMenu = true }) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Menu",
                                        tint = MaterialTheme.colorScheme.onSurface,
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
                                        text = {
                                            Text(if (isDarkTheme) "Light mode" else "Dark mode")
                                        },
                                        onClick = {
                                            showMenu = false
                                            ThemeManager.toggleTheme()
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                                contentDescription = null
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }

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
                        
                        // 1) Pending Medication Tracker
                        item {
                            Text(
                                text = "Pending Medication Tracker",
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
                                Box(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), contentAlignment = Alignment.Center) {
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
                                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(userFullName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Cancel, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text("${med.title} - ${med.timeString}", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Elderly Health Readings",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        if (healthReadings.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Text("No Health Readings found yet.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 16.sp)
                                }
                            }
                        } else {
                            items(healthReadings.size) { index ->
                                val reading = healthReadings[index]
                                val userFullName = elderlyUsersMap[reading.userId] ?: "Elderly User"
                                
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(userFullName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                                            
                                            Spacer(modifier = Modifier.weight(1f))
                                            Text(
                                                text = reading.date,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                                        
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "BP: ${reading.systolic}/${reading.diastolic} mmHg",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("HR: ${reading.heartRate} bpm", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                                                Text("W: ${reading.weight} kg", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                                            }
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
