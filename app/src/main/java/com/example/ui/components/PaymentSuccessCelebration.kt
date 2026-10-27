package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Stars
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.local.entity.RewardEntity
import com.example.data.local.entity.TransactionEntity
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.RoyalBlue500
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.SurfaceWhite
import kotlinx.coroutines.delay
import kotlin.random.Random

data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val radius: Float
)

@Composable
fun PaymentSuccessCelebrationDialog(
    transaction: TransactionEntity,
    reward: RewardEntity,
    onClaimReward: (RewardEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var isScratched by remember { mutableStateOf(reward.isScratched) }
    var scratchProgress by remember { mutableFloatStateOf(if (reward.isScratched) 1f else 0f) }

    val scaleAnim = remember { Animatable(0.2f) }
    val coinScale = remember { Animatable(0.5f) }
    val confettiProgress = remember { Animatable(0f) }

    // Generate random confetti particles
    val particles = remember {
        val colors = listOf(
            Color(0xFFFFD700), Color(0xFFFF4081), Color(0xFF00E676),
            Color(0xFF00B0FF), Color(0xFFFF9100), Color(0xFFE040FB)
        )
        List(40) {
            ConfettiParticle(
                x = 0.5f,
                y = 0.35f,
                vx = (Random.nextFloat() - 0.5f) * 1.5f,
                vy = -Random.nextFloat() * 1.4f,
                color = colors.random(),
                radius = Random.nextFloat() * 8f + 4f
            )
        }
    }

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.65f, stiffness = 400f)
        )
        coinScale.animateTo(
            targetValue = 1.15f,
            animationSpec = tween(400, easing = FastOutSlowInEasing)
        )
        coinScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.5f)
        )
        confettiProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1200, easing = LinearEasing)
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.72f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            // Confetti Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val p = confettiProgress.value
                val w = size.width
                val h = size.height

                particles.forEach { particle ->
                    val curX = (particle.x + particle.vx * p) * w
                    val curY = (particle.y + particle.vy * p + 1.2f * p * p) * h
                    if (curY <= h) {
                        drawCircle(
                            color = particle.color.copy(alpha = (1f - p * 0.7f).coerceIn(0f, 1f)),
                            radius = particle.radius,
                            center = Offset(curX, curY)
                        )
                    }
                }
            }

            // Celebration Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scaleAnim.value)
                    .clip(RoundedCornerShape(28.dp))
                    .testTag("celebration_card"),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Success Checkmark Badge
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Emerald500.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = Emerald600,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Payment Successful!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Navy900
                    )

                    Text(
                        text = "Paid to ${transaction.contactName}",
                        fontSize = 14.sp,
                        color = Slate500,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Text(
                        text = "₹${"%.2f".format(transaction.amount)}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Navy900,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Text(
                        text = "Ref: ${transaction.upiRefId}",
                        fontSize = 11.sp,
                        color = Slate500
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Scratch Card Section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFFFFFBEB), Color(0xFFFEF3C7))
                                )
                            )
                            .border(1.5.dp, Amber500.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Celebration,
                                    contentDescription = "Celebration",
                                    tint = Amber500,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "GUARANTEED CASHBACK",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309),
                                    letterSpacing = 1.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Interactive Scratch Card Area
                            Box(
                                modifier = Modifier
                                    .size(width = 220.dp, height = 110.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .testTag("scratch_card_area")
                                    .clickable {
                                        if (!isScratched) {
                                            scratchProgress = 1f
                                            isScratched = true
                                            onClaimReward(reward)
                                        }
                                    }
                                    .pointerInput(Unit) {
                                        detectDragGestures { _, _ ->
                                            if (!isScratched) {
                                                scratchProgress += 0.08f
                                                if (scratchProgress >= 0.6f) {
                                                    isScratched = true
                                                    scratchProgress = 1f
                                                    onClaimReward(reward)
                                                }
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                // Revealed Reward Underneath
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFFFFF8E1))
                                        .padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MonetizationOn,
                                        contentDescription = "Coin",
                                        tint = Amber500,
                                        modifier = Modifier.size(34.dp)
                                    )
                                    Text(
                                        text = "₹${reward.amount.toInt()} Cashback!",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFB45309)
                                    )
                                    Text(
                                        text = "Credited to Primary Bank",
                                        fontSize = 11.sp,
                                        color = Slate500
                                    )
                                }

                                // Scratch Foil Overlay
                                if (scratchProgress < 1f) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.linearGradient(
                                                    colors = listOf(
                                                        Color(0xFFCBD5E1),
                                                        Color(0xFF94A3B8),
                                                        Color(0xFF64748B)
                                                    )
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.Default.Stars,
                                                contentDescription = "Scratch icon",
                                                tint = SurfaceWhite,
                                                modifier = Modifier.size(28.dp)
                                            )
                                            Text(
                                                text = "Tap or Scratch Here!",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SurfaceWhite,
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action Buttons
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("done_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600)
                    ) {
                        Text(
                            text = "Done",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SurfaceWhite
                        )
                    }
                }
            }
        }
    }
}
