@file:OptIn(ExperimentalMaterial3Api::class)

package com.eldercare.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.activity.compose.BackHandler
import com.eldercare.app.ui.theme.ElderCareGreen
import com.eldercare.app.ui.theme.ElderlyDashboardWavyHeaderShape
import com.eldercare.app.ui.theme.MockupBodyGray
import com.eldercare.app.ui.theme.MockupBottomBarSurface
import com.eldercare.app.ui.theme.MockupCardSurface
import com.eldercare.app.ui.theme.MockupHeaderBlue
import com.eldercare.app.ui.theme.MockupMedicationMissedSalmon
import com.eldercare.app.ui.theme.MockupMedicationTakenBright
import com.eldercare.app.ui.theme.MockupNotificationBadgeBlue
import com.eldercare.app.ui.theme.MockupNotificationBellGold
import com.eldercare.app.ui.theme.MockupReminderPillYellow
import com.eldercare.app.ui.theme.MockupScreenBackground
import com.eldercare.app.ui.theme.MockupTitleBlack
import com.eldercare.app.ui.theme.MedicationMissedRed
import com.eldercare.app.ui.theme.ThemeManager

@Composable
fun ElderlyDashboardScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToSetHealthReading: () -> Unit,
    onNavigateToSetMedication: () -> Unit,
    onNavigateToSetAppointment: () -> Unit,
    onNavigateToReadingResultsMonthList: () -> Unit,
    onNavigateToNotificationReadingResultsList: () -> Unit,
    onNavigateToNotificationMissedMedication: () -> Unit,
    onNavigateToHealthHistoryMissedMedication: () -> Unit,
    onNavigateToReminderAlert: (String) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val elderlyFont = 24.sp
    val userName by viewModel.userName.collectAsState()
    val reminders by viewModel.reminders.collectAsState()
    val todayMissedMedications by viewModel.todayMissedMedications.collectAsState()
    val unreadNotificationCount by viewModel.unreadNotificationCount.collectAsState()

    var selectedBottomTab by rememberSaveable { mutableStateOf(0) }
    val isDarkTheme by ThemeManager.isDarkTheme.collectAsState()
    val dashboardBackground =
        if (isDarkTheme) MaterialTheme.colorScheme.background else MockupScreenBackground

    BackHandler(enabled = selectedBottomTab != 0) {
        selectedBottomTab = 0
    }

    Scaffold(
        containerColor = dashboardBackground,
        bottomBar = {
            ElderlyBottomBar(
                selectedItem = selectedBottomTab,
                onItemSelected = { selectedBottomTab = it },
                notificationCount = unreadNotificationCount,
                useMockupLightChrome = !isDarkTheme
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Section with missed medication info
            TopHeader(
                userName = userName,
                onNavigateToSettings = onNavigateToSettings,
                missedMedicationCount = todayMissedMedications.size,
                useMockupLightChrome = !isDarkTheme
            )

            // Content based on Bottom Tab
            when (selectedBottomTab) {
                0 -> {
                    // Home content – Reminders + Medication Tracker (NO Health Readings here)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(top = 6.dp, bottom = 26.dp)
                    ) {
                        // Reminders section (mockup: white card, green pill rows)
                        item {
                            val upcomingMedications = reminders.filter {
                                it.isMedication && it.medicationStatus == DashboardViewModel.MED_STATUS_PENDING
                            }
                            val upcomingAppointments = reminders.filter { !it.isMedication && !it.isCompleted }
                            DashboardSection(
                                title = "Reminders",
                                useMockupLightChrome = !isDarkTheme
                            ) {
                                if (upcomingMedications.isEmpty() && upcomingAppointments.isEmpty()) {
                                    Text(
                                        "No upcoming reminders",
                                        color = if (isDarkTheme) MaterialTheme.colorScheme.onSurfaceVariant else MockupBodyGray,
                                        fontSize = elderlyFont,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                } else {
                                    upcomingMedications.forEach { reminder ->
                                        ReminderCard(
                                            icon = Icons.Default.Alarm,
                                            title = reminder.title,
                                            time = reminder.timeString,
                                            isCompleted = false,
                                            isActionable = false,
                                            fontSize = elderlyFont,
                                            useMockupLightChrome = !isDarkTheme,
                                            onClick = { onNavigateToReminderAlert(reminder.id) },
                                            onDelete = { viewModel.deleteReminder(reminder.id) }
                                        )
                                    }
                                    upcomingAppointments.forEach { reminder ->
                                        ReminderCard(
                                            icon = Icons.Default.CalendarMonth,
                                            title = reminder.title,
                                            time = reminder.timeString,
                                            isCompleted = reminder.isCompleted,
                                            isActionable = false,
                                            fontSize = elderlyFont,
                                            useMockupLightChrome = !isDarkTheme,
                                            onClick = { onNavigateToReminderAlert(reminder.id) },
                                            onDelete = { viewModel.deleteReminder(reminder.id) }
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            val medReminders = reminders.filter {
                                it.isMedication && it.medicationStatus != DashboardViewModel.MED_STATUS_PENDING
                            }
                            DashboardSection(
                                title = "Medication Tracker",
                                useMockupLightChrome = !isDarkTheme
                            ) {
                                if (medReminders.isEmpty()) {
                                    Text(
                                        "No medications scheduled",
                                        color = if (isDarkTheme) MaterialTheme.colorScheme.onSurfaceVariant else MockupBodyGray,
                                        fontSize = elderlyFont,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                } else {
                                    medReminders.forEach { reminder ->
                                        MedicationTrackerCard(
                                            title = reminder.title,
                                            time = reminder.timeString,
                                            isTaken = reminder.medicationStatus == DashboardViewModel.MED_STATUS_DONE,
                                            isMissed = reminder.medicationStatus == DashboardViewModel.MED_STATUS_MISSED,
                                            fontSize = elderlyFont,
                                            useMockupLightChrome = !isDarkTheme,
                                            onClick = { onNavigateToReminderAlert(reminder.id) },
                                            onDelete = { viewModel.deleteReminder(reminder.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Set Reminder / Appointment / Medication
                    val pageTitleColor = if (isDarkTheme) MaterialTheme.colorScheme.onBackground else Color.Black
                    val cardColor = if (isDarkTheme) MaterialTheme.colorScheme.surface else Color.White
                    val onCard = if (isDarkTheme) MaterialTheme.colorScheme.onSurface else Color.Black
                    val chevronTint = if (isDarkTheme) MaterialTheme.colorScheme.onSurfaceVariant else Color.Black
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
                    ) {
                        item {
                            Text("Set Reminder", fontSize = 28.sp, color = pageTitleColor, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { onNavigateToSetHealthReading() },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = cardColor)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddCircleOutline,
                                        contentDescription = null,
                                        tint = chevronTint,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text("Health Reading Results", fontSize = 16.sp, color = onCard)
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = chevronTint)
                                }
                            }
                        }
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { onNavigateToSetMedication() },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = cardColor)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddCircleOutline,
                                        contentDescription = null,
                                        tint = chevronTint,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text("Set Medication", fontSize = 16.sp, color = onCard)
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = chevronTint)
                                }
                            }
                        }
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { onNavigateToSetAppointment() },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = cardColor)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddCircleOutline,
                                        contentDescription = null,
                                        tint = chevronTint,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text("Set Appointment", fontSize = 16.sp, color = onCard)
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = chevronTint)
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Notifications – now includes Health Readings
                    val pageTitleColor = if (isDarkTheme) MaterialTheme.colorScheme.onBackground else Color.Black
                    val cardColor = if (isDarkTheme) MaterialTheme.colorScheme.surface else Color.White
                    val onCard = if (isDarkTheme) MaterialTheme.colorScheme.onSurface else Color.Black
                    val chevronTint = if (isDarkTheme) MaterialTheme.colorScheme.onSurfaceVariant else Color.Black
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
                    ) {
                        item {
                            Text("Notifications", fontSize = 28.sp, color = pageTitleColor, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { onNavigateToNotificationReadingResultsList() },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = cardColor)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 28.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Reading Results", fontSize = 20.sp, color = onCard)
                                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = chevronTint)
                                }
                            }
                        }
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { onNavigateToNotificationMissedMedication() },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = cardColor)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 28.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Missed Medications", fontSize = 20.sp, color = onCard)
                                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = chevronTint)
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // Health History
                    val pageTitleColor = if (isDarkTheme) MaterialTheme.colorScheme.onBackground else Color.Black
                    val cardColor = if (isDarkTheme) MaterialTheme.colorScheme.surface else Color.White
                    val onCard = if (isDarkTheme) MaterialTheme.colorScheme.onSurface else Color.Black
                    val chevronTint = if (isDarkTheme) MaterialTheme.colorScheme.onSurfaceVariant else Color.Black
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
                    ) {
                        item {
                            Text("Health History", fontSize = 28.sp, color = pageTitleColor, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { onNavigateToReadingResultsMonthList() },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = cardColor)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 28.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Reading Results", fontSize = 20.sp, color = onCard)
                                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = chevronTint)
                                }
                            }
                        }
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { onNavigateToHealthHistoryMissedMedication() },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = cardColor)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 28.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Missed Medications", fontSize = 20.sp, color = onCard)
                                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = chevronTint)
                                }
                            }
                        }
                    }
                }
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Feature coming soon", color = Color.Gray, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun TopHeader(
    userName: String,
    onNavigateToSettings: () -> Unit,
    missedMedicationCount: Int = 0,
    useMockupLightChrome: Boolean = true
) {
    val headerFill =
        if (useMockupLightChrome) MockupHeaderBlue
        else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
    val profileSurface =
        if (useMockupLightChrome) MockupCardSurface else MaterialTheme.colorScheme.surface
    val onHeader =
        if (useMockupLightChrome) MockupTitleBlack else MaterialTheme.colorScheme.onSurface
    val iconTint =
        if (useMockupLightChrome) MockupTitleBlack else MaterialTheme.colorScheme.onSurface

    val isDarkTheme by ThemeManager.isDarkTheme.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        // Reference: deeper blue wave that fully contains avatar + welcome text.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(176.dp)
                .clip(ElderlyDashboardWavyHeaderShape)
                .background(headerFill)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(start = 18.dp, end = 10.dp, top = 18.dp, bottom = 22.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(profileSurface)
                            .border(
                                width = 2.dp,
                                color = if (useMockupLightChrome) Color(0xFFD9D9D9) else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonOutline,
                            contentDescription = "Profile",
                            tint = if (useMockupLightChrome) MockupBodyGray else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Welcome, $userName!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = onHeader,
                        maxLines = 2,
                        lineHeight = 22.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { ThemeManager.toggleTheme() },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = iconTint,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Settings",
                            tint = iconTint,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        if (missedMedicationCount > 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (useMockupLightChrome) MockupMedicationMissedSalmon.copy(alpha = 0.35f)
                        else MedicationMissedRed.copy(alpha = 0.18f)
                    )
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "⚠ $missedMedicationCount missed medication${if (missedMedicationCount > 1) "s" else ""} today",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (useMockupLightChrome) MockupTitleBlack else MedicationMissedRed
                )
            }
        }
    }
}

@Composable
fun DashboardSection(
    title: String,
    titleFontSize: androidx.compose.ui.unit.TextUnit = 24.sp,
    useMockupLightChrome: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardColor =
        if (useMockupLightChrome) MockupCardSurface else MaterialTheme.colorScheme.surface
    val titleColor =
        if (useMockupLightChrome) MockupTitleBlack else MaterialTheme.colorScheme.onSurface
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (useMockupLightChrome) 3.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = title,
                fontSize = titleFontSize,
                fontWeight = FontWeight.Bold,
                color = titleColor
            )
            Spacer(modifier = Modifier.height(12.dp))
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
    fontSize: androidx.compose.ui.unit.TextUnit = 24.sp,
    useMockupLightChrome: Boolean = true,
    onClick: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val pillColor =
        if (useMockupLightChrome) MockupReminderPillYellow
        else Color(0xFFF9A825).copy(alpha = 0.3f)
    val fg =
        if (useMockupLightChrome) MockupTitleBlack else MaterialTheme.colorScheme.onSecondaryContainer

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(pillColor)
            .clickable { onClick() }
            .padding(start = 18.dp, end = 6.dp, top = 14.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = fg,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = "$title - $time",
            fontSize = fontSize,
            color = fg,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Delete",
                tint = fg.copy(alpha = 0.45f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun MedicationTrackerCard(
    title: String,
    time: String,
    isTaken: Boolean,
    isMissed: Boolean,
    fontSize: androidx.compose.ui.unit.TextUnit = 24.sp,
    useMockupLightChrome: Boolean = true,
    onClick: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val statusIcon = if (isTaken) Icons.Default.CheckCircleOutline else Icons.Default.Cancel
    val pillBg = when {
        isTaken -> Color(0xFFCCF6DA)
        isMissed -> Color(0xFFFFD8D8)
        useMockupLightChrome -> MockupMedicationMissedSalmon
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val fg =
        if (useMockupLightChrome) MockupTitleBlack else MaterialTheme.colorScheme.onSurface
    val iconTint = when {
        isTaken -> Color(0xFF1B8A3A)
        isMissed -> Color(0xFFD32F2F)
        useMockupLightChrome -> MockupTitleBlack.copy(alpha = 0.85f)
        else -> MaterialTheme.colorScheme.primary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(pillBg)
            .clickable { onClick() }
            .padding(start = 16.dp, end = 6.dp, top = 14.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = statusIcon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "$title - $time",
            fontSize = fontSize,
            color = fg,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Delete",
                tint = fg.copy(alpha = 0.45f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ElderlyBottomBar(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
    notificationCount: Int = 0,
    useMockupLightChrome: Boolean = true
) {
    val barColor =
        if (useMockupLightChrome) MockupBottomBarSurface else MaterialTheme.colorScheme.surface
    val selectedTint =
        if (useMockupLightChrome) Color(0xFF4A6572) else MaterialTheme.colorScheme.primary
    val unselectedTint =
        if (useMockupLightChrome) MockupBodyGray else MaterialTheme.colorScheme.onSurfaceVariant
    val navColors = NavigationBarItemDefaults.colors(
        selectedIconColor = selectedTint,
        unselectedIconColor = unselectedTint,
        selectedTextColor = selectedTint,
        unselectedTextColor = unselectedTint,
        indicatorColor = Color.Transparent
    )

    NavigationBar(
        containerColor = barColor,
        tonalElevation = if (useMockupLightChrome) 6.dp else 8.dp
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(26.dp)
                )
            },
            label = {
                Text(
                    "Home",
                    fontSize = 9.5.sp,
                    lineHeight = 11.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            },
            selected = selectedItem == 0,
            onClick = { onItemSelected(0) },
            colors = navColors
        )
        NavigationBarItem(
            icon = {
                SetReminderTabIcon(
                    tint = if (selectedItem == 1) selectedTint else unselectedTint
                )
            },
            label = {
                Text(
                    "Set reminder\nAppt · Med",
                    fontSize = 8.75.sp,
                    lineHeight = 10.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    maxLines = 2
                )
            },
            selected = selectedItem == 1,
            onClick = { onItemSelected(1) },
            colors = navColors
        )
        NavigationBarItem(
            icon = {
                val bellTint = when {
                    useMockupLightChrome -> MockupNotificationBellGold
                    selectedItem == 2 -> selectedTint
                    else -> unselectedTint
                }
                Box {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notification",
                        modifier = Modifier.size(26.dp),
                        tint = bellTint
                    )
                    if (notificationCount > 0) {
                        Badge(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 10.dp, y = (-2).dp),
                            containerColor = if (useMockupLightChrome) {
                                MockupNotificationBadgeBlue
                            } else {
                                MaterialTheme.colorScheme.error
                            }
                        ) {
                            Text(
                                "$notificationCount",
                                fontSize = 10.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            },
            label = {
                Text(
                    "Notification",
                    fontSize = 9.5.sp,
                    lineHeight = 11.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            },
            selected = selectedItem == 2,
            onClick = { onItemSelected(2) },
            colors = navColors
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Assignment,
                    contentDescription = "Health History",
                    modifier = Modifier.size(26.dp)
                )
            },
            label = {
                Text(
                    "Health\nHistory",
                    fontSize = 9.5.sp,
                    lineHeight = 11.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    maxLines = 2
                )
            },
            selected = selectedItem == 3,
            onClick = { onItemSelected(3) },
            colors = navColors
        )
    }
}

@Composable
private fun SetReminderTabIcon(tint: Color) {
    Box(modifier = Modifier.size(30.dp)) {
        Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = null,
            modifier = Modifier
                .size(26.dp)
                .align(Alignment.Center),
            tint = tint
        )
        Box(
            modifier = Modifier
                .size(16.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 2.dp, y = 2.dp)
                .clip(CircleShape)
                .background(ElderCareGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(11.dp)
            )
        }
    }
}
