package com.eldercare.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationMissedMedicationScreen(
    onNavigateBack: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val reminders by viewModel.reminders.collectAsState()
    
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Month") }

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
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
                            text = "Missed\nMedications",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            lineHeight = 28.sp
                        )
                    }
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filter", tint = Color.Black)
                    }
                }
            }
        }
    ) { innerPadding ->
        if (showFilterDialog) {
            Dialog(
                onDismissRequest = { showFilterDialog = false },
                properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFB0AEA9), RoundedCornerShape(16.dp))
                        .padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "View History By",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(16.dp)
                        )
                        HorizontalDivider(thickness = 1.dp, color = Color.Gray)
                        
                        // Month Option
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedFilter = "Month" }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            RadioButton(
                                selected = selectedFilter == "Month",
                                onClick = { selectedFilter = "Month" },
                                colors = RadioButtonDefaults.colors(selectedColor = Color.Black, unselectedColor = Color.Black)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Month", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        HorizontalDivider(thickness = 1.dp, color = Color.Gray)
                        
                        // Week Option
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedFilter = "Week" }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            RadioButton(
                                selected = selectedFilter == "Week",
                                onClick = { selectedFilter = "Week" },
                                colors = RadioButtonDefaults.colors(selectedColor = Color.Black, unselectedColor = Color.Black)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Week", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Selecting an option will\nfilter your history.",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { showFilterDialog = false },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF336699))
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = androidx.compose.material.icons.Icons.Default.Check, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Confirm", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val missedMedications = reminders.filter { it.isMedication && !it.isCompleted }

            if (missedMedications.isNotEmpty()) {
                missedMedications.forEach { med ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .background(Color(0xFFA1C6E8), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Text("Medication Name:", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text(med.title, fontSize = 16.sp, color = Color.Black)
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text("Date:", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text(
                                med.date.ifBlank { "Today" },
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text("Time:", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text(med.timeString, fontSize = 16.sp, color = Color.Black)
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFE2E0D8), RoundedCornerShape(8.dp))
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "Missed Dose !", 
                                        fontSize = 20.sp, 
                                        fontWeight = FontWeight.Bold, 
                                        color = Color(0xFFE65C5C)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "This medication was not confirmed as taken. We will log this on your records.",
                                        fontSize = 14.sp,
                                        color = Color.Black,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Text("No missed medications to display.", color = Color.Gray, modifier = Modifier.padding(top = 100.dp))
            }
        }
    }
}

