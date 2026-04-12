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
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eldercare.app.R
import com.eldercare.app.data.model.UserRole
import com.eldercare.app.ui.theme.*

/**
 * Screen 2: Login Screen
 * Matches the provided design with logo, email/password fields,
 * Log In button, Forgot password link, and Register link.
 */
@Composable
fun LoginScreen(
    role: String = "elderly",
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: (UserRole) -> Unit,
    onForgotPassword: (String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val expectedRole = if (role == "caregiver") UserRole.CAREGIVER else UserRole.ELDERLY

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current
    
    // Handle successful login
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess && uiState.userRole != null) {
            android.widget.Toast.makeText(context, "Login Successfully", android.widget.Toast.LENGTH_SHORT).show()
            onLoginSuccess(uiState.userRole!!)
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
                .fillMaxHeight(0.30f)
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
                .padding(horizontal = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // ── Logo ─────────────────────────────────────────
            Image(
                painter = painterResource(id = R.drawable.eldercare_logo),
                contentDescription = "ElderCare Logo",
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(2.dp))

            // ── App Name ─────────────────────────────────────
            Text(
                text = "ElderCare",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = ElderCareBlue,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Self Health Monitoring App",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = ElderCareGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── Sign-In Heading ──────────────────────────────
            Text(
                text = "Sign-In",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = ElderCareDarkBlue,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Email Field ──────────────────────────────────
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = {
                    Text(
                        text = "Email or Username",
                        fontSize = 16.sp,
                        color = ElderCareGray
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = null,
                        tint = ElderCareBlue,
                        modifier = Modifier.size(24.dp)
                    )
                },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 17.sp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = ElderCareDarkBlue,
                    unfocusedTextColor = ElderCareDarkBlue,
                    cursorColor = ElderCareBlue,
                    focusedBorderColor = ElderCareBlue,
                    unfocusedBorderColor = OutlineLight,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ── Password Field ───────────────────────────────
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = {
                    Text(
                        text = "Password",
                        fontSize = 16.sp,
                        color = ElderCareGray
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = ElderCareGray.copy(alpha = 0.6f),
                        modifier = Modifier.size(24.dp)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff
                            else Icons.Filled.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = ElderCareGray
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 17.sp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = ElderCareDarkBlue,
                    unfocusedTextColor = ElderCareDarkBlue,
                    cursorColor = ElderCareBlue,
                    focusedBorderColor = ElderCareBlue,
                    unfocusedBorderColor = OutlineLight,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.login(email.trim(), password, expectedRole)
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
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
                    fontSize = 14.sp,
                    color = ErrorLight,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Log In Button ────────────────────────────────
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.login(email.trim(), password, expectedRole)
                },
                modifier = Modifier
                    .width(140.dp)
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
                        text = "Log In",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Forgot Password ──────────────────────────────
            Text(
                text = "- Forgot password -",
                fontSize = 13.sp,
                color = ElderCareGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onForgotPassword(email.trim())
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            // ── Register Prompt ──────────────────────────────
            Row(
                modifier = Modifier.padding(bottom = 40.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "No account yet?  ",
                    fontSize = 15.sp,
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
                        ) { onNavigateToRegister() }
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Register",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ElderCareGreen
                    )
                }
            }
        }
    }
}
