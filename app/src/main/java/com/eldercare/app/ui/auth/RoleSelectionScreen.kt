package com.eldercare.app.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eldercare.app.R
import com.eldercare.app.ui.theme.*

/**
 * Screen 1: Role Selection
 * User picks "Elderly" or "Caregiver" before proceeding to login.
 */
@Composable
fun RoleSelectionScreen(
    onElderlySelected: () -> Unit,
    onCaregiverSelected: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ── Light blue gradient at the bottom ────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            ElderCareLightBlue.copy(alpha = 0.4f),
                            ElderCareLightBlue.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // ── Logo ─────────────────────────────────────────
            Image(
                painter = painterResource(id = R.drawable.eldercare_logo),
                contentDescription = "ElderCare Logo",
                modifier = Modifier.size(140.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(4.dp))

            // ── App Name ─────────────────────────────────────
            Text(
                text = "ElderCare",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = ElderCareBlue,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Self Health Monitoring App",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = ElderCareGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // ── Sign In as ───────────────────────────────────
            Text(
                text = "Sign In as",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = ElderCareDarkBlue,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Elderly Button ───────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 2.dp,
                        color = ElderCareDarkBlue,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(Color.White)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onElderlySelected() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Elderly",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElderCareDarkBlue
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "or",
                fontSize = 16.sp,
                color = ElderCareGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Caregiver Button ─────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 2.dp,
                        color = ElderCareLightGreen,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(ElderCareLightGreen.copy(alpha = 0.3f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onCaregiverSelected() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Caregiver",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElderCareDarkBlue
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
