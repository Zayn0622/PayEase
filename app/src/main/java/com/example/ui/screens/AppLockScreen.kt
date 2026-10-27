package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.R
import com.example.security.BiometricAuthManager
import com.example.security.BiometricAvailability
import com.example.security.BiometricResult
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Navy900
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.RoyalBlue700
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.SurfaceWhite

@Composable
fun AppLockScreen(
    onUnlockSuccess: () -> Unit,
    onUsePinFallback: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    val biometricAvailability = remember {
        BiometricAuthManager.checkAvailability(context)
    }

    // Pulse animation for fingerprint target
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    fun triggerBiometrics() {
        if (activity != null) {
            BiometricAuthManager.authenticate(
                activity = activity,
                title = "Unlock PayEase",
                subtitle = "Confirm your fingerprint or face to proceed",
                description = "Bank-grade biometric authentication secures your account",
                negativeButtonText = "Use UPI PIN",
                onResult = { result ->
                    when (result) {
                        is BiometricResult.Success -> {
                            onUnlockSuccess()
                        }
                        is BiometricResult.FallbackToPin -> {
                            onUsePinFallback()
                        }
                        is BiometricResult.Error -> {
                            // If hardware error / cancelled, allow PIN fallback
                            onUsePinFallback()
                        }
                        is BiometricResult.Failed -> {
                            // User can retry touching sensor
                        }
                    }
                }
            )
        } else {
            onUsePinFallback()
        }
    }

    // Auto prompt on first launch if biometric is available
    LaunchedEffect(Unit) {
        triggerBiometrics()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Dark slate navy
                        Color(0xFF1E1B4B), // Deep royal purple
                        Color(0xFF0F172A)
                    )
                )
            )
            .padding(horizontal = 24.dp, vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            // Top branding & Security indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 24.dp)
            ) {
                // PayEase Logo
                Image(
                    painter = painterResource(id = R.drawable.ic_payease_logo),
                    contentDescription = "PayEase Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceWhite)
                        .border(2.dp, Color(0xFFC084FC), RoundedCornerShape(20.dp))
                        .padding(4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "PayEase",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = SurfaceWhite,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Security Shield Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF312E81).copy(alpha = 0.6f))
                        .border(1.dp, Color(0xFF6366F1), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = null,
                        tint = Emerald600,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Bank-Grade Security Active",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFA5B4FC)
                    )
                }
            }

            // Center: Interactive Biometric Touch Target
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 24.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(130.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF8B5CF6).copy(alpha = 0.35f),
                                    Color(0xFF6366F1).copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                        .border(2.dp, Color(0xFFA855F7).copy(alpha = 0.8f), CircleShape)
                        .clickable { triggerBiometrics() }
                        .testTag("biometric_sensor_button")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2E1065))
                            .border(1.dp, Color(0xFFC084FC), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Fingerprint,
                            contentDescription = "Biometric Sensor",
                            tint = Color(0xFFE9D5FF),
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Biometric Authentication",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SurfaceWhite
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Touch the fingerprint sensor or use Face ID to unlock PayEase",
                    fontSize = 14.sp,
                    color = Slate400,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Status chip
                val statusText = when (biometricAvailability) {
                    BiometricAvailability.AVAILABLE -> "Biometric Sensor Ready"
                    BiometricAvailability.NOT_ENROLLED -> "Biometrics Supported (PIN fallback ready)"
                    BiometricAvailability.NO_HARDWARE -> "Standard PIN / Biometric Sandbox"
                    BiometricAvailability.UNAVAILABLE -> "Secure Enclave Active"
                }

                Text(
                    text = "Status: $statusText",
                    fontSize = 12.sp,
                    color = Color(0xFF93C5FD),
                    fontWeight = FontWeight.Medium
                )
            }

            // Bottom Actions & Fallbacks
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                // Primary Biometric Prompt Button
                Button(
                    onClick = { triggerBiometrics() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoyalBlue600
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("unlock_biometric_action_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Fingerprint,
                        contentDescription = null,
                        tint = SurfaceWhite,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Unlock with Biometrics",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurfaceWhite
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Secondary UPI PIN Fallback Button
                OutlinedButton(
                    onClick = { onUsePinFallback() },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFC7D2FE)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFF6366F1), Color(0xFFA855F7))
                        )
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("unlock_upi_pin_fallback_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Pin,
                        contentDescription = null,
                        tint = Color(0xFFC7D2FE),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Use 4-Digit UPI PIN",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 256-bit AES & NPCI Security Footer
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = Slate400,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Protected by 256-Bit AES Hardware KeyStore",
                        fontSize = 11.sp,
                        color = Slate400,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
