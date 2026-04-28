package com.eldercare.app.ui.dashboard

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetMedicationScreen(
    onNavigateBack: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    var medicationName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var medicationTime by remember { mutableStateOf("") }

    val context = LocalContext.current

    val calendar = Calendar.getInstance()
    // Time Picker for Medication (12-hour with AM/PM)
    val timePickerDialog = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val cal = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                }
                medicationTime = SimpleDateFormat("hh:mm a", Locale.US).format(cal.time)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false // 12-hour format with AM/PM
        )
    }

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
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Set Medication",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        lineHeight = 28.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Medication Scheduling",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFCCF6DA), RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFF5CB85C), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text("Medication Name", fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        BasicTextField(
                            value = medicationName,
                            onValueChange = { medicationName = it },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(fontSize = 16.sp, color = Color.Black)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(modifier = Modifier.weight(2f)) {
                            Text("Dosage", fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier.fillMaxWidth().height(44.dp)
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                BasicTextField(
                                    value = dosage,
                                    onValueChange = { dosage = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = TextStyle(fontSize = 16.sp, color = Color.Black)
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Time", fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier.fillMaxWidth().height(44.dp)
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .clickable { timePickerDialog.show() }
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = medicationTime.ifBlank { "Set" },
                                        fontSize = 14.sp,
                                        color = if (medicationTime.isBlank()) Color.DarkGray else Color.Black
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = "Pick time",
                                        tint = Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                medicationName = ""; dosage = ""; medicationTime = ""
                            }
                        ) {
                            Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete", tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete", fontSize = 14.sp, color = Color.Black)
                        }
                    }
                }
            }

            Button(
                onClick = {
                    if (medicationName.isNotBlank() && dosage.isNotBlank() && medicationTime.isNotBlank()) {
                        viewModel.setMedication(medicationName, dosage, medicationTime)
                        android.widget.Toast.makeText(context, "Saved Successfully", android.widget.Toast.LENGTH_SHORT).show()
                        onNavigateBack()
                    } else {
                        android.widget.Toast.makeText(context, "Please fill in all medication fields", android.widget.Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5CB85C))
            ) {
                Text("Set", fontSize = 18.sp, color = Color.White)
            }
        }
    }
}
