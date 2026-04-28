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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.outlined.Delete

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingResultsMonthListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    isFromNotifications: Boolean = false,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    // We now use the month-grouped data or notification data from the ViewModel dynamically
    val readingsByMonth by viewModel.healthReadingsByMonth.collectAsState()
    val notificationReadings by viewModel.notificationHealthReadings.collectAsState()
    
    val displayData = if (isFromNotifications) {
        val monthFormat = java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.US)
        val dateFormats = listOf(
            java.text.SimpleDateFormat("MMMM d, yyyy", java.util.Locale.US),
            java.text.SimpleDateFormat("MM/dd/yyyy", java.util.Locale.US),
            java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        )
        notificationReadings.groupBy { reading ->
            var parsed: java.util.Date? = null
            for (fmt in dateFormats) {
                try {
                    parsed = fmt.parse(reading.date.trim())
                    if (parsed != null) break
                } catch (e: Exception) {}
            }
            parsed?.let { monthFormat.format(it) } ?: "Today"
        }
    } else {
        readingsByMonth
    }

    val elderlyUsersMap by viewModel.elderlyUsersMap.collectAsState()
    val userRole by viewModel.userRole.collectAsState()
    val isCaregiver = userRole == "caregiver"
    val context = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
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
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Reading Results",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (displayData.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(if (isFromNotifications) "No new reading results for today" else "No reading results history", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 16.sp)
                    }
                }
            } else {
                displayData.forEach { (month, readings) ->
                    // Month Header
                    item {
                        Text(
                            text = month,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                    
                    // Items for that month
                    items(readings) { reading ->
                        val elderlyLine = if (isCaregiver) {
                            elderlyUsersMap[reading.userId]?.let { "Elderly: $it" }
                                ?: "Elderly ID: ${reading.userId.ifBlank { "—" }}"
                        } else null
                        val sys = reading.systolic.toIntOrNull() ?: 0
                        val dia = reading.diastolic.toIntOrNull() ?: 0
                        val isNormal = sys in 1..120 && dia in 1..80
                        val statusText = if (isNormal) "NORMAL" else "ABNORMAL"
                        val statusColor = if (isNormal) androidx.compose.ui.graphics.Color(0xFF4CAF50) else androidx.compose.ui.graphics.Color(0xFFF44336)
                        val fieldBgColor = if (isNormal) androidx.compose.ui.graphics.Color(0xFFE8F5E9) else androidx.compose.ui.graphics.Color(0xFFFFEBEE)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToDetail(reading.id) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                if (elderlyLine != null) {
                                    Text(
                                        elderlyLine,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                val formattedDate = try {
                                    val cal = java.util.Calendar.getInstance()
                                    val dateFormats = listOf(
                                        java.text.SimpleDateFormat("MMMM d, yyyy", java.util.Locale.US),
                                        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                                    )
                                    var parsed: java.util.Date? = null
                                    for (fmt in dateFormats) {
                                        try {
                                            parsed = fmt.parse(reading.date)
                                            if (parsed != null) break
                                        } catch (e: Exception) {}
                                    }
                                    parsed?.let { java.text.SimpleDateFormat("MM/dd/yyyy", java.util.Locale.US).format(it) } ?: reading.date
                                } catch (e: Exception) { reading.date }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(fieldBgColor, RoundedCornerShape(8.dp))
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(formattedDate, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.Black)
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(statusText, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = statusColor)
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(
                                                onClick = {
                                                    if (isFromNotifications) {
                                                        viewModel.dismissReadingResultNotification(reading.id)
                                                        android.widget.Toast.makeText(context, "Dismissed from notifications", android.widget.Toast.LENGTH_SHORT).show()
                                                    } else {
                                                        viewModel.deleteHealthReading(reading.id)
                                                        android.widget.Toast.makeText(context, "Deleted from history", android.widget.Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            ) {
                                                Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete", tint = androidx.compose.ui.graphics.Color(0xFFC62828))
                                            }
                                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = androidx.compose.ui.graphics.Color.Black)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
