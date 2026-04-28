package com.eldercare.app.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eldercare.app.R
import com.eldercare.app.ui.theme.ElderCareBlue
import com.eldercare.app.ui.theme.ElderCareGray
import com.eldercare.app.ui.theme.ElderCareGreen
import com.eldercare.app.ui.theme.MockupCaregiverButtonBorder
import com.eldercare.app.ui.theme.MockupCaregiverButtonFill
import com.eldercare.app.ui.theme.MockupElderlyButtonBorder
import com.eldercare.app.ui.theme.MockupElderlyButtonFill
import com.eldercare.app.ui.theme.MockupRoleScreenBackground
import com.eldercare.app.ui.theme.MockupTitleBlack

/**
 * Screen 1: Role selection — layout matched to reference (solid background, ~80% width buttons,
 * logo/title spacing, thin borders, medium-weight labels).
 */
@Composable
fun RoleSelectionScreen(
    onElderlySelected: () -> Unit,
    onCaregiverSelected: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MockupRoleScreenBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .fillMaxWidth(0.8f)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(52.dp))

            Image(
                painter = painterResource(id = R.drawable.eldercare_logo),
                contentDescription = "ElderCare Logo",
                modifier = Modifier.size(180.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Elder",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElderCareBlue
                )
                Text(
                    text = "Care",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElderCareGreen
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Self Health Monitoring App",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = ElderCareGray,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                letterSpacing = 0.2.sp
            )

            // Large gap between branding and role selection (reference)
            Spacer(modifier = Modifier.height(56.dp))

            Text(
                text = "Sign In as",
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = MockupTitleBlack,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(22.dp))

            RoleChoiceButton(
                label = "Elderly",
                fill = MockupElderlyButtonFill,
                borderColor = MockupElderlyButtonBorder,
                onClick = onElderlySelected
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "or",
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = MockupTitleBlack,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            RoleChoiceButton(
                label = "Caregiver",
                fill = MockupCaregiverButtonFill,
                borderColor = MockupCaregiverButtonBorder,
                onClick = onCaregiverSelected
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun RoleChoiceButton(
    label: String,
    fill: Color,
    borderColor: Color,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    Card(
        modifier = Modifier
            .width(350.dp)
            .height(87.dp),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = fill),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 36.sp,
                fontWeight = FontWeight.Medium,
                color = MockupTitleBlack
            )
        }
    }
}
