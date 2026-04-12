package com.eldercare.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationReadingResultScreen(
    month: String,
    onNavigateBack: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val readings by viewModel.healthReadings.collectAsState()
    val reading = readings.firstOrNull { it.id == month }
    
    val systolic = reading?.systolic?.toIntOrNull() ?: 0
    val diastolic = reading?.diastolic?.toIntOrNull() ?: 0
    val dateText = reading?.date?.ifBlank { "Unknown Date" } ?: "Unknown Date"

    val isAbnormal = systolic >= 130 || diastolic >= 85

    Scaffold(
        containerColor = Color(0xFFF9FAFC),
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFA1C6E8), RoundedCornerShape(16.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Appointment Date", fontSize = 18.sp, color = Color.Black)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(dateText, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Icon(imageVector = Icons.Outlined.CalendarToday, contentDescription = null, tint = Color.Black)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text("Reading Result", fontSize = 18.sp, color = Color.Black)
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("$systolic / $diastolic", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E75B6))
                        Text("mmHg", fontSize = 14.sp, color = Color.DarkGray)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isAbnormal) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFECA39A), RoundedCornerShape(8.dp))
                                .padding(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Outlined.Cancel, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("ABNORMAL", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF8CE39B), RoundedCornerShape(8.dp))
                                .padding(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Outlined.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("NORMAL", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { },
                enabled = false,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(disabledContainerColor = Color(0xFFE0E0E0))
            ) {
                Text("Set", fontSize = 18.sp, color = Color.White)
            }
        }
    }
}
