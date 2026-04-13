package com.eldercare.app.ui.dashboard

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CalendarToday
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
fun HealthReadingMedicationScreen(
    onNavigateBack: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    var systolic by remember { mutableStateOf("") }
    var diastolic by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var heartRate by remember { mutableStateOf("") }

    var medicationName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var medicationTime by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Date Picker for Health Reading
    val calendar = Calendar.getInstance()
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                date = SimpleDateFormat("MMMM d, yyyy", Locale.US).format(cal.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

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
                        text = "Health Reading\nResults",
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
            // Health Reading Form
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Blood Pressure Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFA1C6E8), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Blood Pressure", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Systolic", fontSize = 16.sp, color = Color.Black)
                        BasicTextField(
                            value = systolic,
                            onValueChange = { systolic = it.filter { c -> c.isDigit() } },
                            modifier = Modifier.fillMaxWidth().height(48.dp).padding(top = 4.dp),
                            textStyle = TextStyle(fontSize = 18.sp, color = Color.Black, textAlign = TextAlign.Center),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        HorizontalDivider(color = Color.Black, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                        Text("Diastolic", fontSize = 16.sp, color = Color.Black)
                        BasicTextField(
                            value = diastolic,
                            onValueChange = { diastolic = it.filter { c -> c.isDigit() } },
                            modifier = Modifier.fillMaxWidth().height(48.dp).padding(top = 4.dp),
                            textStyle = TextStyle(fontSize = 18.sp, color = Color.Black, textAlign = TextAlign.Center),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }

                // Date, Weight, Heart Rate Column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Date - with calendar picker
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("Date", fontSize = 16.sp, color = Color.Black)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .background(Color(0xFFA1C6E8), RoundedCornerShape(8.dp))
                                .clickable { datePickerDialog.show() }
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = date.ifBlank { "Select date" },
                                    fontSize = 14.sp,
                                    color = if (date.isBlank()) Color.DarkGray else Color.Black,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Pick date",
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Weight - auto-append "kg"
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("Weight", fontSize = 16.sp, color = Color.Black)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .background(Color(0xFFA1C6E8), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BasicTextField(
                                    value = weight,
                                    onValueChange = { weight = it.filter { c -> c.isDigit() || c == '.' } },
                                    modifier = Modifier.weight(1f),
                                    textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                )
                                if (weight.isNotBlank()) {
                                    Text("kg", fontSize = 16.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }

                    // Heart Rate / Pulse - text field input
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("Heart Rate / Pulse", fontSize = 16.sp, color = Color.Black)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .background(Color(0xFFA1C6E8), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BasicTextField(
                                    value = heartRate,
                                    onValueChange = { heartRate = it.filter { c -> c.isDigit() } },
                                    modifier = Modifier.weight(1f),
                                    textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                                if (heartRate.isNotBlank()) {
                                    Text("bpm", fontSize = 14.sp, color = Color.DarkGray)
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    if (systolic.isNotBlank() && diastolic.isNotBlank() && date.isNotBlank() && weight.isNotBlank() && heartRate.isNotBlank()) {
                        viewModel.saveHealthReading(systolic, diastolic, date, weight, heartRate)
                        android.widget.Toast.makeText(context, "Saved Successfully", android.widget.Toast.LENGTH_SHORT).show()
                        onNavigateBack()
                    } else {
                        android.widget.Toast.makeText(context, "Please fill in all health reading fields", android.widget.Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5CB85C))
            ) {
                Text("Save", fontSize = 18.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Medication Form
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
