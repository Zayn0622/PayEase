package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entity.BankAccountEntity
import com.example.ui.theme.Crimson600
import com.example.ui.theme.Navy900
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.SurfaceWhite
import kotlin.math.roundToInt

data class PaymentFailureData(
    val recipientName: String,
    val recipientVpa: String,
    val amount: Double,
    val bankAccount: BankAccountEntity?,
    val reason: String = "Bank server response timeout (NPCI error 91)",
    val errorCode: String = "U30-DECLINED",
    val canRetry: Boolean = true
)

@Composable
fun PaymentFailureDialog(
    failureData: PaymentFailureData,
    onRetry: () -> Unit,
    onSwitchBank: () -> Unit,
    onDismiss: () -> Unit
) {
    val scaleAnim = remember { Animatable(0.2f) }
    val shakeOffset = remember { Animatable(0f) }
    val rippleScale = remember { Animatable(0.8f) }
    val rippleAlpha = remember { Animatable(0.7f) }

    LaunchedEffect(Unit) {
        // Pop in dialog
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.65f, stiffness = 400f)
        )
        // Wobble / Shake effect
        shakeOffset.animateTo(
            targetValue = -22f,
            animationSpec = tween(60, easing = FastOutSlowInEasing)
        )
        shakeOffset.animateTo(
            targetValue = 22f,
            animationSpec = tween(60, easing = FastOutSlowInEasing)
        )
        shakeOffset.animateTo(
            targetValue = -14f,
            animationSpec = tween(60, easing = FastOutSlowInEasing)
        )
        shakeOffset.animateTo(
            targetValue = 14f,
            animationSpec = tween(60, easing = FastOutSlowInEasing)
        )
        shakeOffset.animateTo(
            targetValue = 0f,
            animationSpec = spring(dampingRatio = 0.6f)
        )
    }

    // Crimson pulse ripple
    LaunchedEffect(Unit) {
        while (true) {
            rippleScale.snapTo(0.8f)
            rippleAlpha.snapTo(0.6f)
            rippleScale.animateTo(1.6f, animationSpec = tween(1200, easing = FastOutSlowInEasing))
            rippleAlpha.animateTo(0f, animationSpec = tween(1200, easing = FastOutSlowInEasing))
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.72f))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scaleAnim.value)
                    .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
                    .clip(RoundedCornerShape(28.dp))
                    .testTag("payment_failure_dialog"),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated Failure Icon with Ripple
                    Box(
                        modifier = Modifier.size(90.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Pulsing red ripple
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .scale(rippleScale.value)
                                .clip(CircleShape)
                                .background(Crimson600.copy(alpha = rippleAlpha.value))
                        )

                        // Main circle
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFFEF4444), Crimson600)
                                    )
                                )
                                .border(3.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Payment Failed",
                                tint = Color.White,
                                modifier = Modifier.size(46.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Payment Failed",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Crimson600
                    )

                    Text(
                        text = "₹${"%.2f".format(failureData.amount)} to ${failureData.recipientName}",
                        fontSize = 15.sp,
                        color = Navy900,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Error Details Callout Box
                    Surface(
                        color = Color(0xFFFEF2F2),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WarningAmber,
                                    contentDescription = "Warning",
                                    tint = Crimson600,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = "Reason for Failure",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Crimson600
                                    )
                                    Text(
                                        text = failureData.reason,
                                        fontSize = 13.sp,
                                        color = Navy900,
                                        lineHeight = 18.sp
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Error Code", fontSize = 11.sp, color = Slate500)
                                Text(
                                    failureData.errorCode,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Crimson600
                                )
                            }

                            failureData.bankAccount?.let { bank ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Debited Account", fontSize = 11.sp, color = Slate500)
                                    Text(
                                        "${bank.bankName} ${bank.accountNumberMasked}",
                                        fontSize = 11.sp,
                                        color = Slate700
                                    )
                                }
                            }

                            Text(
                                text = "No money was deducted from your account. If deducted, it will be refunded within 24-48 hours.",
                                fontSize = 11.sp,
                                color = Slate500,
                                lineHeight = 15.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Retry Button
                    if (failureData.canRetry) {
                        Button(
                            onClick = onRetry,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("retry_payment_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RoyalBlue600
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Retry Payment",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Switch Bank Button
                    OutlinedButton(
                        onClick = onSwitchBank,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("switch_bank_payment_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = null,
                            tint = Slate700,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Choose Another Bank Account",
                            fontWeight = FontWeight.SemiBold,
                            color = Slate700,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Dismiss Button
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("dismiss_payment_failure_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            fontWeight = FontWeight.Medium,
                            color = Slate500,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
