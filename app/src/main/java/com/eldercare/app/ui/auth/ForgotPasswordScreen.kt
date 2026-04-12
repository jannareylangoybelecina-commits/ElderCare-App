package com.eldercare.app.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.MarkEmailRead
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eldercare.app.ui.theme.*

/**
 * Screen 3: Forgot Password
 * Sends a real password reset link to the user's Gmail via Firebase Auth.
 */
@Composable
fun ForgotPasswordScreen(
    email: String = "",
    onNavigateBack: () -> Unit,
    onSave: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val resetState by viewModel.passwordResetState.collectAsStateWithLifecycle()

    var emailInput by remember { mutableStateOf(email) }

    // When successfully sent, we stay on the success screen
    // User can go back when ready

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
                .statusBarsPadding()
                .padding(horizontal = 32.dp)
                .padding(top = 24.dp)
        ) {
            // ── Top Row: Back arrow + Title ──────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                IconButton(
                    onClick = {
                        viewModel.resetPasswordResetState()
                        onNavigateBack()
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back",
                        tint = ElderCareDarkBlue,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Forgot",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElderCareDarkBlue
                    )
                    Text(
                        text = "Password",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElderCareDarkBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = ElderCareDarkBlue.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Show success or the form ─────────────────────
            if (resetState.isSuccess) {
                // ── Success State ────────────────────────────
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    Icon(
                        imageVector = Icons.Outlined.MarkEmailRead,
                        contentDescription = "Email Sent",
                        tint = ElderCareGreen,
                        modifier = Modifier.size(80.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Reset Link Sent!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElderCareDarkBlue,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "We've sent a password reset link to:",
                        fontSize = 15.sp,
                        color = ElderCareGray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = resetState.sentToEmail,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElderCareBlue,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Please check your inbox (and spam folder) for the email. " +
                                "Click the link in the email to set a new password.",
                        fontSize = 14.sp,
                        color = ElderCareGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // ── Back to Login Button ─────────────────
                    Button(
                        onClick = {
                            viewModel.resetPasswordResetState()
                            onNavigateBack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElderCareGreen
                        )
                    ) {
                        Text(
                            text = "Back to Login",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Resend option ────────────────────────
                    Text(
                        text = "Didn't receive the email? Resend",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = ElderCareBlue,
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            viewModel.sendPasswordResetEmail(resetState.sentToEmail)
                        }
                    )
                }
            } else {
                // ── Email Input Form ─────────────────────────
                Text(
                    text = "Enter the email address associated with your account. " +
                            "We'll send you a link to reset your password.",
                    fontSize = 15.sp,
                    color = ElderCareDarkBlue,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ── Email Field ──────────────────────────────
                Text(
                    text = "Email Address",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = ElderCareDarkBlue
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    placeholder = {
                        Text(
                            text = "your.email@gmail.com",
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
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                    shape = RoundedCornerShape(10.dp),
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
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            viewModel.sendPasswordResetEmail(emailInput.trim())
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ── Error Message ────────────────────────────
                AnimatedVisibility(
                    visible = resetState.errorMessage != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        text = resetState.errorMessage ?: "",
                        fontSize = 14.sp,
                        color = ErrorLight,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Send Reset Link Button ───────────────────
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.sendPasswordResetEmail(emailInput.trim())
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElderCareGreen
                    ),
                    enabled = !resetState.isLoading
                ) {
                    if (resetState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text = "Send Reset Link",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
