package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entity.BankAccountEntity
import com.example.data.local.entity.RewardEntity
import com.example.data.local.entity.TransactionEntity
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Navy900
import com.example.ui.theme.RoyalBlue50
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.SurfaceWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@Composable
fun PaymentSuccessDialog(
    transaction: TransactionEntity,
    reward: RewardEntity?,
    bankAccount: BankAccountEntity?,
    onClaimReward: (RewardEntity) -> Unit,
    onDismiss: () -> Unit
) {
    // Animation state
    val scaleAnim = remember { Animatable(0.2f) }
    val checkmarkScale = remember { Animatable(0f) }
    val rippleScale = remember { Animatable(0.8f) }
    val rippleAlpha = remember { Animatable(0.8f) }
    val confettiProgress = remember { Animatable(0f) }

    // Confetti particles
    val particles = remember {
        val colors = listOf(
            Color(0xFFFFD700), Color(0xFFFF4081), Color(0xFF00E676),
            Color(0xFF00B0FF), Color(0xFFFF9100), Color(0xFFE040FB)
        )
        List(48) {
            ConfettiParticle(
                x = 0.5f,
                y = 0.28f,
                vx = (Random.nextFloat() - 0.5f) * 1.8f,
                vy = -Random.nextFloat() * 1.6f,
                color = colors.random(),
                radius = Random.nextFloat() * 7f + 3f
            )
        }
    }

    LaunchedEffect(Unit) {
        // Pop in dialog
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.68f, stiffness = 420f)
        )
        // Pop checkmark
        checkmarkScale.animateTo(
            targetValue = 1.25f,
            animationSpec = tween(280, easing = FastOutSlowInEasing)
        )
        checkmarkScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.5f)
        )
    }

    LaunchedEffect(Unit) {
        confettiProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1400, easing = LinearEasing)
        )
    }

    // Looping gentle ripple around checkmark
    LaunchedEffect(Unit) {
        while (true) {
            rippleScale.snapTo(0.8f)
            rippleAlpha.snapTo(0.6f)
            rippleScale.animateTo(1.6f, animationSpec = tween(1200, easing = FastOutSlowInEasing))
            rippleAlpha.animateTo(0f, animationSpec = tween(1200, easing = FastOutSlowInEasing))
        }
    }

    val formattedDate = remember(transaction.timestamp) {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        sdf.format(Date(transaction.timestamp))
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
            // Confetti canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val p = confettiProgress.value
                val w = size.width
                val h = size.height

                particles.forEach { particle ->
                    val curX = (particle.x + particle.vx * p) * w
                    val curY = (particle.y + particle.vy * p + 1.3f * p * p) * h
                    if (curY <= h) {
                        drawCircle(
                            color = particle.color.copy(alpha = (1f - p * 0.75f).coerceIn(0f, 1f)),
                            radius = particle.radius,
                            center = Offset(curX, curY)
                        )
                    }
                }
            }

            // Success Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scaleAnim.value)
                    .clip(RoundedCornerShape(28.dp))
                    .testTag("payment_success_dialog"),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated Checkmark with Ripple
                    Box(
                        modifier = Modifier.size(90.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Pulsing ripple ring
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .scale(rippleScale.value)
                                .clip(CircleShape)
                                .background(Emerald500.copy(alpha = rippleAlpha.value))
                        )

                        // Main circle
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Emerald500, Emerald600)
                                    )
                                )
                                .border(3.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Payment Done",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(46.dp)
                                    .scale(checkmarkScale.value)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Payment Successful!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Navy900
                    )

                    Text(
                        text = "Paid to ${transaction.contactName}",
                        fontSize = 14.sp,
                        color = Slate500,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    // Large formatted amount
                    Text(
                        text = "₹${"%.2f".format(transaction.amount)}",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        color = Navy900,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    // Transaction Metadata Box
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("To UPI ID", fontSize = 12.sp, color = Slate500)
                                Text(
                                    transaction.contactVpa,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Navy900
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Debited From", fontSize = 12.sp, color = Slate500)
                                Text(
                                    "${bankAccount?.bankName ?: transaction.bankName} ${bankAccount?.accountNumberMasked ?: ""}".trim(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Navy900
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("UPI Ref No.", fontSize = 12.sp, color = Slate500)
                                Text(
                                    transaction.upiRefId,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Slate700
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Time", fontSize = 12.sp, color = Slate500)
                                Text(
                                    formattedDate,
                                    fontSize = 11.sp,
                                    color = Slate500
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Cashback Reward Card / Scratch Incentive
                    reward?.let { rew ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFFFFBEB), Color(0xFFFEF3C7))
                                    )
                                )
                                .border(1.dp, Amber500.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                .padding(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Amber500),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Redeem,
                                        contentDescription = "Reward",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "🎉 Guaranteed Cashback!",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Navy900
                                    )
                                    Text(
                                        text = "You unlocked a scratch card for this transfer",
                                        fontSize = 11.sp,
                                        color = Slate700
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Scratch Cashback Button
                        Button(
                            onClick = { onClaimReward(rew) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("claim_cashback_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Amber500
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Scratch & Reveal Cashback 🎁",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Done Button
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("dismiss_payment_success_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Done",
                            fontWeight = FontWeight.SemiBold,
                            color = Slate700,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
