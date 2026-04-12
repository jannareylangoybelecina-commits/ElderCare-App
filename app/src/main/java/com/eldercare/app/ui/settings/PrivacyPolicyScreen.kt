package com.eldercare.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onNavigateBack: () -> Unit) {
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
                        text = "Privacy Policy",
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
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Last Updated: April 2026",
                fontSize = 13.sp,
                color = Color(0xFF7A8A99)
            )

            PolicySection(
                title = "1. Information We Collect",
                content = "We collect personal information that you provide when creating " +
                        "an account, including your full name, email address, age, phone number, " +
                        "and health data such as blood pressure readings, heart rate, weight, " +
                        "and medication schedules."
            )

            PolicySection(
                title = "2. How We Use Your Information",
                content = "Your information is used to:\n" +
                        "• Provide health monitoring and medication tracking services\n" +
                        "• Share relevant health data with your assigned caregiver\n" +
                        "• Send notifications about missed medications and health readings\n" +
                        "• Improve our services and user experience"
            )

            PolicySection(
                title = "3. Data Storage & Security",
                content = "Your data is stored securely using Firebase, a Google Cloud " +
                        "service, with industry-standard encryption. We implement appropriate " +
                        "security measures to protect your personal information against " +
                        "unauthorized access, alteration, or destruction."
            )

            PolicySection(
                title = "4. Data Sharing",
                content = "We do not sell or rent your personal information to third parties. " +
                        "Your health data is only shared with:\n" +
                        "• Your assigned caregiver(s) whom you authorize\n" +
                        "• Firebase services for data storage and authentication\n" +
                        "We may disclose information if required by law."
            )

            PolicySection(
                title = "5. Your Rights",
                content = "You have the right to:\n" +
                        "• Access and review your personal data\n" +
                        "• Request correction of inaccurate information\n" +
                        "• Delete your account and associated data\n" +
                        "• Opt out of non-essential notifications"
            )

            PolicySection(
                title = "6. Contact Us",
                content = "If you have questions about this Privacy Policy, please contact " +
                        "us at support@eldercare-app.com."
            )
        }
    }
}

@Composable
private fun PolicySection(title: String, content: String) {
    Column {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A3A5C)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            fontSize = 14.sp,
            color = Color(0xFF4A4A4A),
            lineHeight = 22.sp
        )
    }
}
