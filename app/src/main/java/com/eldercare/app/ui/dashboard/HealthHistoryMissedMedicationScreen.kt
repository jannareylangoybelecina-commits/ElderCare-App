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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthHistoryMissedMedicationScreen(
    onNavigateBack: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val reminders by viewModel.reminders.collectAsState()
    val elderlyUsersMap by viewModel.elderlyUsersMap.collectAsState()
    val userRole by viewModel.userRole.collectAsState()
    val isCaregiver = userRole == "caregiver"

    var selectedMonthIndex by remember { mutableStateOf<Int?>(null) }
    
    val monthsLabels = listOf(
        "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", 
        "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
    )

    Scaffold(
        containerColor = Color(0xFFE8EEF5), // Light blue-ish background from layout
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(vertical = 16.dp, horizontal = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        if (selectedMonthIndex != null) {
                            selectedMonthIndex = null
                        } else {
                            onNavigateBack()
                        }
                    }) {
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
        if (selectedMonthIndex == null) {
            // Month List Level
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(12) { index ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedMonthIndex = index },
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
                            Text(monthsLabels[index].substring(0, 1) + monthsLabels[index].substring(1).lowercase(), fontSize = 22.sp, color = Color.Black)
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.Black)
                        }
                    }
                }
            }
        } else {
            // Week Drilldown Level
            val monthIdx = selectedMonthIndex!!
            val monthRecords = reminders.filter {
                it.isMedication && it.medicationStatus == DashboardViewModel.MED_STATUS_MISSED &&
                getMonthIndex(it.date) == monthIdx
            }.sortedBy { getEpochTime(it.date) }
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.White) // Background changes to white inside drilldown according to screenshot
                    .padding(horizontal = 24.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp)
            ) {
                item {
                    Text(
                        text = monthsLabels[monthIdx],
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                for (weekNum in 1..4) {
                    item {
                        Text(
                            text = "WEEK $weekNum",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    val weekRecords = monthRecords.filter { getWeekOfMonth(it.date) == weekNum }
                    
                    if (weekRecords.isEmpty()) {
                        item {
                            Text(
                                "No missed medication.",
                                color = Color(0xFF1565C0), // Blue underlined
                                fontSize = 16.sp,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    } else {
                        // Display items for the week
                        items(weekRecords) { med ->
                            val elderlyLabel = if (isCaregiver) {
                                elderlyUsersMap[med.userId]?.let { "Elderly: $it" } ?: "Elderly ID: ${med.userId.ifBlank { "—" }}"
                            } else null
                            
                            HistoryMissedItemCard(elderlyLabel, med)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryMissedItemCard(elderlyLabel: String?, med: ReminderItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFECA39A), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        if (elderlyLabel != null) {
            Text(elderlyLabel, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        Text("Medication Name:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text(med.title, fontSize = 16.sp, color = Color.Black)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text("Date:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text(med.date, fontSize = 16.sp, color = Color.Black)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text("Time:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text(med.timeString, fontSize = 16.sp, color = Color.Black)
    }
}

private fun getMonthIndex(dateStr: String): Int {
    try {
        val formats = listOf("MMMM d, yyyy", "MM/dd/yyyy", "yyyy-MM-dd")
        val formatter = java.text.SimpleDateFormat()
        for (fmt in formats) {
            try {
                formatter.applyPattern(fmt)
                val d = formatter.parse(dateStr)
                if (d != null) {
                    val cal = java.util.Calendar.getInstance()
                    cal.time = d
                    return cal.get(java.util.Calendar.MONTH)
                }
            } catch (e: Exception) {}
        }
    } catch(e: Exception){}
    return -1
}

private fun getWeekOfMonth(dateStr: String): Int {
    try {
        val formats = listOf("MMMM d, yyyy", "MM/dd/yyyy", "yyyy-MM-dd")
        val formatter = java.text.SimpleDateFormat()
        for (fmt in formats) {
            try {
                formatter.applyPattern(fmt)
                val d = formatter.parse(dateStr)
                if (d != null) {
                    val cal = java.util.Calendar.getInstance()
                    cal.time = d
                    var week = cal.get(java.util.Calendar.WEEK_OF_MONTH)
                    if (week > 4) week = 4 // Align with strictly "Week 1 to Week 4" layout constraints
                    return week
                }
            } catch (e: Exception) {}
        }
    } catch(e: Exception){}
    return 1
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
