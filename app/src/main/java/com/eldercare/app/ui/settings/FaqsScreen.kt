package com.eldercare.app.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqsScreen(onNavigateBack: () -> Unit) {
    val faqs = listOf(
        "What is ElderCare?" to
                "ElderCare is a self health monitoring app designed for elderly users " +
                "and their caregivers. It helps track health readings, medications, " +
                "and appointments in real time.",
        "How do I add a health reading?" to
                "Go to the Home tab, tap 'Set Reminder', then select 'Health Reading Results'. " +
                "Enter your blood pressure, weight, and heart rate values, then tap 'Set' to save.",
        "How does the medication tracker work?" to
                "When you set a medication reminder, it appears on your dashboard. " +
                "If a medication is not confirmed as taken by the scheduled time, " +
                "it will be flagged as a missed dose and your caregiver will be notified.",
        "Can my caregiver see my health data?" to
                "Yes. Caregivers who are linked to your account can view your health " +
                "readings, missed medications, and appointment schedules through their " +
                "Notification tab.",
        "How do I reset my password?" to
                "Go to the login screen and tap 'Forgot password'. Enter your email " +
                "address and we'll send a password reset link to your Gmail inbox.",
        "How do I change notification settings?" to
                "Navigate to Settings > Notification Controls. You can toggle alerts " +
                "for reading results, missed medications, sounds, and vibration.",
        "Is my data secure?" to
                "Yes. ElderCare uses Firebase Authentication and Firestore with " +
                "industry-standard encryption. Your data is stored securely and " +
                "only accessible to you and your assigned caregiver.",
        "How do I contact support?" to
                "Go to Settings > About / Help > Contact Support to reach our " +
                "support team via email."
    )

    Scaffold(
        containerColor = Color.White,
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
                        text = "FAQs",
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
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            faqs.forEach { (question, answer) ->
                FaqItem(question = question, answer = answer)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FaqItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F9FC)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = question,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A3A5C),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = Color(0xFF2B7EC1),
                    modifier = Modifier.size(24.dp)
                )
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Text(
                    text = answer,
                    fontSize = 14.sp,
                    color = Color(0xFF4A4A4A),
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
    }
}
