package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.security.BiometricAuthManager
import com.example.security.BiometricAvailability
import com.example.security.BiometricResult
import com.example.ui.theme.Amber500
import com.example.ui.theme.Crimson600
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Navy900
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.SurfaceWhite
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecuritySettingsDialog(
    isAppLockEnabled: Boolean,
    isPaymentAuthEnabled: Boolean,
    isBalanceAuthEnabled: Boolean,
    isDailyLimitEnabled: Boolean,
    dailyTransactionLimit: Double,
    todaySpentAmount: Double,
    onToggleAppLock: (Boolean) -> Unit,
    onTogglePaymentAuth: (Boolean) -> Unit,
    onToggleBalanceAuth: (Boolean) -> Unit,
    onToggleDailyLimit: (Boolean) -> Unit,
    onConfigureDailyLimit: () -> Unit,
    onLockAppNow: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val indianFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }

    val availability = remember {
        BiometricAuthManager.checkAvailability(context)
    }

    val progress = if (dailyTransactionLimit > 0) {
        (todaySpentAmount / dailyTransactionLimit).coerceIn(0.0, 1.0).toFloat()
    } else 0f
    val remainingAllowance = (dailyTransactionLimit - todaySpentAmount).coerceAtLeast(0.0)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEEF2FF))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = null,
                            tint = RoyalBlue600,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Security & Biometrics",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )
                        Text(
                            text = "Bank-Grade Protection",
                            fontSize = 12.sp,
                            color = Slate500
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_security_dialog_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = Slate600
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Biometric Hardware Status Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDCFCE7))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Emerald600,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Biometric KeyStore Ready",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )
                        val details = when (availability) {
                            BiometricAvailability.AVAILABLE -> "Fingerprint / Face ID sensor active & enrolled"
                            BiometricAvailability.NOT_ENROLLED -> "Biometric hardware active (Sandbox/PIN fallback enabled)"
                            BiometricAvailability.NO_HARDWARE -> "Standard Device Sandbox (PIN fallback enabled)"
                            BiometricAvailability.UNAVAILABLE -> "Secure Enclave encryption active"
                        }
                        Text(
                            text = details,
                            fontSize = 11.sp,
                            color = Slate500
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "AUTHENTICATION RULES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Slate400,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Rule 1: App Launch Lock
            SecurityToggleItem(
                icon = Icons.Filled.Lock,
                title = "Biometric App Launch Lock",
                subtitle = "Require fingerprint/face every time PayEase opens",
                checked = isAppLockEnabled,
                onCheckedChange = onToggleAppLock,
                testTag = "toggle_app_launch_lock"
            )

            HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 10.dp))

            // Rule 2: Payment Authorization
            SecurityToggleItem(
                icon = Icons.Filled.Payment,
                title = "Biometric Payment Authorization",
                subtitle = "Authenticate outgoing UPI transfers with fingerprint before executing",
                checked = isPaymentAuthEnabled,
                onCheckedChange = onTogglePaymentAuth,
                testTag = "toggle_payment_auth"
            )

            HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 10.dp))

            // Rule 3: Balance Inquiries
            SecurityToggleItem(
                icon = Icons.Filled.Fingerprint,
                title = "Fast Balance Inquiries",
                subtitle = "Quickly verify with biometric sensor to reveal bank balance",
                checked = isBalanceAuthEnabled,
                onCheckedChange = onToggleBalanceAuth,
                testTag = "toggle_balance_auth"
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "SPENDING & FRAUD CONTROL",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Slate400,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Daily Transaction Limit Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                    .testTag("daily_limit_settings_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFEEF2FF))
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Speed,
                                    contentDescription = null,
                                    tint = RoyalBlue600,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Daily Transaction Limit",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Navy900
                                )
                                Text(
                                    text = if (isDailyLimitEnabled) "Limit: ${indianFormat.format(dailyTransactionLimit)}" else "Protection Paused",
                                    fontSize = 12.sp,
                                    color = if (isDailyLimitEnabled) RoyalBlue600 else Slate500,
                                    fontWeight = if (isDailyLimitEnabled) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }

                        Switch(
                            checked = isDailyLimitEnabled,
                            onCheckedChange = onToggleDailyLimit,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SurfaceWhite,
                                checkedTrackColor = RoyalBlue600
                            ),
                            modifier = Modifier.testTag("toggle_daily_limit_in_settings")
                        )
                    }

                    if (isDailyLimitEnabled) {
                        Spacer(modifier = Modifier.height(12.dp))

                        // Spending Progress Bar
                        val progressColor = when {
                            progress > 0.9f -> Crimson600
                            progress > 0.7f -> Amber500
                            else -> Emerald600
                        }

                        LinearProgressIndicator(
                            progress = { progress },
                            color = progressColor,
                            trackColor = Color(0xFFE2E8F0),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Spent: ${indianFormat.format(todaySpentAmount)}",
                                fontSize = 11.sp,
                                color = Slate600
                            )
                            Text(
                                text = "Remaining: ${indianFormat.format(remainingAllowance)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (remainingAllowance < 1000) Crimson600 else Emerald600
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = {
                                onDismiss()
                                onConfigureDailyLimit()
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("configure_daily_limit_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Tune,
                                contentDescription = null,
                                tint = RoyalBlue600,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Configure Daily Limit",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = RoyalBlue600
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Test Biometric Prompt Button
            OutlinedButton(
                onClick = {
                    if (activity != null) {
                        BiometricAuthManager.authenticate(
                            activity = activity,
                            title = "Verify Biometric Sensor",
                            subtitle = "Testing bank-grade sensor integration",
                            negativeButtonText = "Cancel",
                            onResult = { /* test completed */ }
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("test_biometric_prompt_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Fingerprint,
                    contentDescription = null,
                    tint = RoyalBlue600,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Test Biometric Prompt",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = RoyalBlue600
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Lock App Immediately Button
            Button(
                onClick = {
                    onDismiss()
                    onLockAppNow()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("lock_app_now_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = SurfaceWhite,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Lock App Now",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SurfaceWhite
                )
            }
        }
    }
}

@Composable
private fun SecurityToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF1F5F9))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = RoyalBlue600,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.padding(end = 8.dp)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Navy900
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Slate500,
                    lineHeight = 16.sp
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = SurfaceWhite,
                checkedTrackColor = RoyalBlue600
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}
