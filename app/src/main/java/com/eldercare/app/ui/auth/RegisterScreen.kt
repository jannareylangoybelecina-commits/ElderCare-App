package com.eldercare.app.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eldercare.app.R
import com.eldercare.app.data.model.UserRole
import com.eldercare.app.ui.theme.*

/**
 * Screen 4: Registration Screen
 * Shows logo, form fields, optional caregiver contact, and register button.
 */
@Composable
fun RegisterScreen(
    role: String = "elderly",
    viewModel: AuthViewModel = hiltViewModel(),
    onRegistrationSuccess: (UserRole) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    var fullName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Caregiver contact (optional)
    var caregiverName by remember { mutableStateOf("") }
    var caregiverPhone by remember { mutableStateOf("") }

    val expectedRole = if (role == "caregiver") UserRole.CAREGIVER else UserRole.ELDERLY
    val context = androidx.compose.ui.platform.LocalContext.current

    // Handle successful registration
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess && uiState.userRole != null) {
            android.widget.Toast.makeText(context, "Created Successfully", android.widget.Toast.LENGTH_SHORT).show()
            onRegistrationSuccess(uiState.userRole!!)
            viewModel.resetState()
        }
    }

    // Handle errors (Toast fallback)
    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            android.widget.Toast.makeText(context, uiState.errorMessage, android.widget.Toast.LENGTH_LONG).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ── Light blue gradient at the bottom ────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.18f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            ElderCareLightBlue.copy(alpha = 0.35f),
                            ElderCareLightBlue.copy(alpha = 0.65f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp)
                .padding(top = 44.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Logo (smaller) ───────────────────────────────
            Image(
                painter = painterResource(id = R.drawable.eldercare_logo),
                contentDescription = "ElderCare Logo",
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "ElderCare",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ElderCareBlue
            )

            Text(
                text = "Self Health Monitoring App",
                fontSize = 11.sp,
                color = ElderCareGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Heading ──────────────────────────────────────
            Text(
                text = if (role == "caregiver") "Create Caregiver Account" else "Create your account",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = ElderCareDarkBlue,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Full Name ────────────────────────────────────
            RegisterField(
                value = fullName,
                onValueChange = { fullName = it },
                placeholder = "Full Name",
                leadingIcon = { FieldIcon(Icons.Outlined.Person) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ── Age + Phone No. (side by side) OR just Phone No. ─
            if (role == "elderly") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Age field
                    OutlinedTextField(
                        value = age,
                        onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 3) age = it },
                        placeholder = {
                            Text("Age", fontSize = 15.sp, color = ElderCareGray)
                        },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 15.sp),
                        shape = RoundedCornerShape(10.dp),
                        colors = registerFieldColors(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Right) }
                        ),
                        modifier = Modifier
                            .weight(0.35f)
                            .height(54.dp)
                    )

                    // Phone No. field
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        placeholder = {
                            Text("Phone No.", fontSize = 15.sp, color = ElderCareGray)
                        },
                        leadingIcon = { FieldIcon(Icons.Outlined.Phone) },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 15.sp),
                        shape = RoundedCornerShape(10.dp),
                        colors = registerFieldColors(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        modifier = Modifier
                            .weight(0.65f)
                            .height(54.dp)
                    )
                }
            } else {
                RegisterField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    placeholder = "Phone No.",
                    leadingIcon = { FieldIcon(Icons.Outlined.Phone) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── Email Address ────────────────────────────────
            RegisterField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Email Address",
                leadingIcon = { FieldIcon(Icons.Outlined.Email) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ── Password ─────────────────────────────────────
            RegisterField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                leadingIcon = { FieldIcon(Icons.Outlined.Lock) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff
                            else Icons.Filled.Visibility,
                            contentDescription = null,
                            tint = ElderCareGray,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ── Confirm Password ─────────────────────────────
            RegisterField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = "Confirm Password",
                leadingIcon = { FieldIcon(Icons.Outlined.Lock) },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Filled.VisibilityOff
                            else Icons.Filled.Visibility,
                            contentDescription = null,
                            tint = ElderCareGray,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Caregiver's Contact (Optional) ───────────────
            Text(
                text = "Caregiver's Contact (Optional)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ElderCareDarkBlue,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            RegisterField(
                value = caregiverName,
                onValueChange = { caregiverName = it },
                placeholder = "Caregiver's Name",
                leadingIcon = { FieldIcon(Icons.Outlined.Person) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            RegisterField(
                value = caregiverPhone,
                onValueChange = { caregiverPhone = it },
                placeholder = "Caregiver's Phone No.",
                leadingIcon = { FieldIcon(Icons.Outlined.Phone) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Error Message ────────────────────────────────
            AnimatedVisibility(
                visible = uiState.errorMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = uiState.errorMessage ?: "",
                    fontSize = 13.sp,
                    color = ErrorLight,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Register Button ──────────────────────────────
            Button(
                onClick = {
                    focusManager.clearFocus()
                    if (password != confirmPassword) {
                        // Handled by ViewModel validation or local check
                        return@Button
                    }
                    if (role == "caregiver") {
                        viewModel.registerCaregiver(
                            email = email.trim(),
                            password = password,
                            fullName = fullName.trim(),
                            contactNumber = phoneNumber.trim()
                        )
                    } else {
                        viewModel.registerElderly(
                            email = email.trim(),
                            password = password,
                            fullName = fullName.trim(),
                            age = age.toIntOrNull() ?: 0,
                            gender = "", // not required in this design
                            contactNumber = phoneNumber.trim()
                        )
                    }
                },
                modifier = Modifier
                    .width(150.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElderCareGreen
                ),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        text = "Register",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Sign In Prompt ───────────────────────────────
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account?  ",
                    fontSize = 14.sp,
                    color = ElderCareGray
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = 1.5.dp,
                            color = ElderCareGreen,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onNavigateBack() }
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "Sign In",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ElderCareGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── Reusable registration field ──────────────────────────────
@Composable
private fun RegisterField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                fontSize = 15.sp,
                color = ElderCareGray
            )
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(fontSize = 15.sp),
        shape = RoundedCornerShape(10.dp),
        colors = registerFieldColors(),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
    )
}

@Composable
private fun FieldIcon(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = ElderCareGray.copy(alpha = 0.7f),
        modifier = Modifier.size(22.dp)
    )
}

@Composable
private fun registerFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = ElderCareDarkBlue,
    unfocusedTextColor = ElderCareDarkBlue,
    cursorColor = ElderCareBlue,
    focusedBorderColor = ElderCareBlue,
    unfocusedBorderColor = OutlineLight,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White
)
