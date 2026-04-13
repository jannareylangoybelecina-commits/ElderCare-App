package com.eldercare.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Schedule
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

@Composable
fun ReminderAlertScreen(
    reminderId: String,
    onNavigateBack: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val reminders by viewModel.reminders.collectAsState()
    val reminder = reminders.find { it.id == reminderId }
        ?: reminders.find { it.id.hashCode().toString() == reminderId }

    if (reminder == null) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF8B0000)), contentAlignment = Alignment.Center) {
            Text("Reminder not found", color = Color.White)
            Button(onClick = onNavigateBack, modifier = Modifier.padding(top = 16.dp)) {
                Text("Go Back")
            }
        }
        return
    }

    val isMedication = reminder.isMedication
    val typeText = if (isMedication) "Medication" else "Appointment"
    val instructionText = if (isMedication) "Time to Take ${reminder.title}" else reminder.title

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF6F0E0D)) // Deep crimson red
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Glowing Icon Box
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isMedication) Icons.Outlined.MedicalServices else Icons.Outlined.CalendarToday,
                contentDescription = null,
                tint = Color(0xFFFFDAB9), // Peach/orange glow
                modifier = Modifier.size(80.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Reminder Alert!", fontSize = 26.sp, color = Color(0xFFFFE0CC), fontWeight = FontWeight.Normal)

        Spacer(modifier = Modifier.height(24.dp))

        // Info Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x26FFFFFF), RoundedCornerShape(12.dp)) // Lighter semi-transparent overlay
                .padding(vertical = 32.dp, horizontal = 16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(typeText, fontSize = 20.sp, color = Color(0xFFFFDAB9), fontWeight = FontWeight.Normal)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    instructionText,
                    fontSize = 18.sp,
                    color = Color(0xFFFFDAB9),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Outlined.Schedule, contentDescription = null, tint = Color(0xFFFFDAB9), modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(reminder.timeString, fontSize = 32.sp, color = Color(0xFFFFDAB9))
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Mark as DONE Button
        Button(
            onClick = {
                viewModel.markReminderCompleted(reminder.id)
                onNavigateBack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(2.dp, Color(0xFF00C853), RoundedCornerShape(24.dp)), // Bright green stroke
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mark as DONE", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}
