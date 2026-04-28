package com.eldercare.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageProfileScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val profile by viewModel.userProfile.collectAsState()

    var fullName by remember(profile.fullName) { mutableStateOf(profile.fullName) }
    var email by remember(profile.email) { mutableStateOf(profile.email) }
    var phone by remember(profile.phone) { mutableStateOf(profile.phone) }
    var birthday by remember(profile.birthday) { mutableStateOf(profile.birthday) }
    var gender by remember(profile.gender) { mutableStateOf(profile.gender) }
    var address by remember(profile.address) { mutableStateOf(profile.address) }
    var phoneValidationMessage by remember { mutableStateOf<String?>(null) }
    
    var showLogoutConfirmation by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current

    if (showLogoutConfirmation) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmation = false },
            title = { Text("Logout", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black) },
            text = { Text("Are you sure you want to logout?", fontSize = 16.sp) },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutConfirmation = false
                    android.widget.Toast.makeText(context, "Logout Successfully", android.widget.Toast.LENGTH_SHORT).show()
                    onLogout()
                }) {
                    Text("Logout", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmation = false }) {
                    Text("Cancel", color = Color.DarkGray)
                }
            }
        )
    }

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
                                modifier = Modifier.size(24.dp),
                                tint = Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(32.dp))
                        Text(
                            text = "Manage\nprofile",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            lineHeight = 28.sp
                        )
                    }
                    Text(
                        text = "Save",
                        color = Color(0xFF5BA4E5),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable {
                                if (phone.length != 11) {
                                    android.widget.Toast.makeText(
                                        context,
                                        "Phone number must be exactly 11 digits.",
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
                                    return@clickable
                                }
                                val updated = viewModel.updateProfile(
                                    UserProfile(
                                        fullName, email, phone, birthday, gender, address
                                    )
                                )
                                if (updated) {
                                    android.widget.Toast.makeText(context, "Saved Successfully", android.widget.Toast.LENGTH_SHORT).show()
                                    onNavigateBack()
                                } else {
                                    android.widget.Toast.makeText(
                                        context,
                                        "Phone number must be exactly 11 digits.",
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            // Profile image placeholder
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEEF5FD)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PersonOutline,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(50.dp),
                    tint = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ProfileField("Full Name", fullName) { fullName = it }
                ProfileField("Email", email, readOnly = true) { email = it }
                ProfileField(
                    label = "Phone",
                    value = phone,
                    keyboardType = KeyboardType.Number
                ) { input ->
                    val digitsOnly = input.filter { it.isDigit() }
                    if (input != digitsOnly) {
                        phoneValidationMessage = "Please enter numbers only."
                    } else {
                        phoneValidationMessage = null
                    }
                    phone = digitsOnly.take(11)
                }
                if (phoneValidationMessage != null || (phone.isNotBlank() && phone.length != 11)) {
                    Text(
                        text = phoneValidationMessage ?: "Phone number must be exactly 11 digits.",
                        fontSize = 12.sp,
                        color = Color(0xFFD32F2F)
                    )
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(x = (-24).dp)
                        .background(Color(0xFFEEF5FD))
                        .padding(vertical = 4.dp, horizontal = 24.dp)
                ) {
                    Text(
                        text = "Personal Info",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                ProfileField("Birthday", birthday) { birthday = it }
                ProfileField("Gender", gender) { gender = it }
                ProfileField("Address", address) { address = it }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { showLogoutConfirmation = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Logout", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProfileField(
    label: String,
    value: String,
    readOnly: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(text = label, fontSize = 16.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                androidx.compose.foundation.text.BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    readOnly = readOnly,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = keyboardType
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp, color = Color.Black),
                    modifier = Modifier.weight(1f)
                )
                if (!readOnly) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
