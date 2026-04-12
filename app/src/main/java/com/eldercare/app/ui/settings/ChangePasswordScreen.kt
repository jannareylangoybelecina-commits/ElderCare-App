package com.eldercare.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(onNavigateBack: () -> Unit) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
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
                            text = "Change\nPassword",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            lineHeight = 28.sp
                        )
                    }
                    Text(
                        text = "Save",
                        color = Color(0xFF5BA4E5),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable {
                                if (oldPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
                                    android.widget.Toast.makeText(context, "Please fill in all fields", android.widget.Toast.LENGTH_SHORT).show()
                                } else if (newPassword != confirmPassword) {
                                    android.widget.Toast.makeText(context, "Passwords do not match", android.widget.Toast.LENGTH_SHORT).show()
                                } else {
                                    android.widget.Toast.makeText(context, "Saved Successfully", android.widget.Toast.LENGTH_SHORT).show()
                                    onNavigateBack()
                                }
                            }
                            .padding(end = 8.dp)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PasswordField("Old Password", oldPassword, false) { oldPassword = it }
            
            PasswordField(
                "New Password", 
                newPassword, 
                true,
                isPasswordVisible = newPasswordVisible,
                onVisibilityToggle = { newPasswordVisible = !newPasswordVisible }
            ) { newPassword = it }

            PasswordField(
                "Confirm Password", 
                confirmPassword, 
                true,
                isPasswordVisible = confirmPasswordVisible,
                onVisibilityToggle = { confirmPasswordVisible = !confirmPasswordVisible }
            ) { confirmPassword = it }
        }
    }
}

@Composable
fun PasswordField(
    label: String, 
    value: String, 
    showEyeIcon: Boolean, 
    isPasswordVisible: Boolean = false,
    onVisibilityToggle: () -> Unit = {},
    onValueChange: (String) -> Unit
) {
    Column {
        Text(text = label, fontSize = 14.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp, color = Color.Black),
                    visualTransformation = if (isPasswordVisible || !showEyeIcon) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.weight(1f)
                )
                if (showEyeIcon) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Toggle Visibility",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onVisibilityToggle() }
                    )
                }
            }
        }
    }
}
