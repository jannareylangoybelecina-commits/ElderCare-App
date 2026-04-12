package com.eldercare.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
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
fun AccessibilityScreen(onNavigateBack: () -> Unit) {
    var selectedTheme by remember { mutableStateOf("Light") }
    var sliderPosition by remember { mutableFloatStateOf(1f) }
    var selectedLanguage by remember { mutableStateOf("English") }

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
                    Spacer(modifier = Modifier.width(32.dp))
                    Text(
                        text = "Accessibility",
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
                .verticalScroll(rememberScrollState())
        ) {
            Text("Theme", fontSize = 20.sp, color = Color.Black, modifier = Modifier.padding(bottom = 16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp, 120.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF0EBE1))
                            .border(width = 2.dp, color = if (selectedTheme == "Light") Color.Black else Color.Transparent, shape = RoundedCornerShape(8.dp))
                            .clickable { selectedTheme = "Light" }
                    ) {
                        // Dummy UI for Light Theme icon block
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Light", fontSize = 16.sp, color = Color.Black)
                    RadioButton(
                        selected = selectedTheme == "Light",
                        onClick = { selectedTheme = "Light" }
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp, 120.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF3B3E42))
                            .border(width = 2.dp, color = if (selectedTheme == "Dark") Color.Black else Color.Transparent, shape = RoundedCornerShape(8.dp))
                            .clickable { selectedTheme = "Dark" }
                    ) {
                        // Dummy UI for Dark Theme icon block
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Dark", fontSize = 16.sp, color = Color.Black)
                    RadioButton(
                        selected = selectedTheme == "Dark",
                        onClick = { selectedTheme = "Dark" }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Font size", fontSize = 20.sp, color = Color.Black, modifier = Modifier.padding(bottom = 16.dp))

            Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                valueRange = 0f..2f,
                steps = 1,
                colors = SliderDefaults.colors(
                    thumbColor = Color.Transparent,
                    activeTrackColor = Color(0xFFCBE6F3),
                    inactiveTrackColor = Color(0xFFCBE6F3)
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text("small", fontSize = 12.sp, color = Color.Black)
                Text("medium", fontSize = 16.sp, color = Color.Black, modifier = Modifier.padding(bottom = 2.dp))
                Text("large", fontSize = 24.sp, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Language Settings", fontSize = 20.sp, color = Color.Black, modifier = Modifier.padding(bottom = 16.dp))

            val languages = listOf("English", "Tagalog", "Cebuano")
            languages.forEach { lang ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedLanguage = lang }
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = selectedLanguage == lang,
                        onClick = { selectedLanguage = lang }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = lang, fontSize = 16.sp, color = Color.Black)
                }
            }
        }
    }
}
