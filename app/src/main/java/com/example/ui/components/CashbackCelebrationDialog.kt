package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.local.entity.RewardEntity
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Navy900
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.SurfaceWhite
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * Celebratory Scratch Card & Popping Coin animation dialog triggered after successful payment.
 */
@Composable
fun CashbackCelebrationDialog(
    reward: RewardEntity,
    onClaimReward: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    var isRevealed by remember { mutableStateOf(reward.isScratched) }
    var scratchPercentage by remember { mutableFloatStateOf(if (reward.isScratched) 100f else 0f) }
    val scratchPaths = remember { mutableStateListOf<Offset>() }

    // Coin Popping & Confetti Animation
    val scaleAnim = remember { Animatable(0.4f) }
    val coinBounce = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(400, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(isRevealed) {
        if (isRevealed) {
            coinBounce.animateTo(
                targetValue = -30f,
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            )
            coinBounce.animateTo(
                targetValue = 0f,
                animationSpec = tween(200, easing = FastOutSlowInEasing)
            )
            onClaimReward(reward.amount)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xCC070E1A))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background Confetti Particles
            ConfettiParticles(isRevealed = isRevealed)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scaleAnim.value)
                    .clip(RoundedCornerShape(28.dp))
                    .background(SurfaceWhite)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEF3C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Redeem,
                                contentDescription = "Reward",
                                tint = Amber500,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Guaranteed Cashback",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Navy900
                            )
                            Text(
                                text = "PayEase Rewards Club",
                                fontSize = 12.sp,
                                color = Slate500
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Slate500
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Interactive Scratch Card Area
                Box(
                    modifier = Modifier
                        .size(width = 280.dp, height = 280.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .shadow(12.dp, RoundedCornerShape(24.dp))
                        .testTag("scratch_card_box"),
                    contentAlignment = Alignment.Center
                ) {
                    // 1. REVEALED PRIZE LAYER (Bottom)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFFFFBEB), Color(0xFFFEF3C7))
                                )
                            )
                            .border(3.dp, Color(0xFFFDE68A), RoundedCornerShape(24.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Popping Coin Graphic
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .offset { IntOffset(0, coinBounce.value.roundToInt()) }
                                    .shadow(8.dp, CircleShape)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFD700)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_cashback_reward),
                                    contentDescription = "Cashback Coin",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "You Won",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Slate700
                            )

                            Text(
                                text = "₹${"%.0f".format(reward.amount)}",
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Black,
                                color = Emerald600
                            )

                            Text(
                                text = "Instant Cashback Credited!",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Slate500,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // 2. SCRATCHABLE COVER LAYER (Top)
                    if (!isRevealed) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(RoyalBlue600, Color(0xFF1E3A8A), Color(0xFF3B82F6))
                                    )
                                )
                                .pointerInput(Unit) {
                                    detectDragGestures { change, _ ->
                                        change.consume()
                                        scratchPaths.add(change.position)
                                        scratchPercentage = (scratchPaths.size * 2f).coerceAtMost(100f)
                                        if (scratchPercentage >= 35f) {
                                            isRevealed = true
                                        }
                                    }
                                }
                        ) {
                            // Scratch lines drawn
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                if (scratchPaths.isNotEmpty()) {
                                    val path = Path().apply {
                                        moveTo(scratchPaths.first().x, scratchPaths.first().y)
                                        scratchPaths.forEach { lineTo(it.x, it.y) }
                                    }
                                    drawPath(
                                        path = path,
                                        color = Color.White.copy(alpha = 0.5f),
                                        style = Stroke(width = 50f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ElectricBolt,
                                    contentDescription = "Bolt",
                                    tint = Color(0xFFFDE047),
                                    modifier = Modifier.size(48.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "SCRATCH & WIN",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = SurfaceWhite,
                                    letterSpacing = 1.2.sp
                                )

                                Text(
                                    text = "Swipe finger to reveal your\nguaranteed cashback",
                                    fontSize = 13.sp,
                                    color = Color(0xFFDBEAFE),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Button
                if (isRevealed) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("claim_done_button")
                    ) {
                        Text(
                            text = "Add to Bank Account",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SurfaceWhite
                        )
                    }
                } else {
                    Button(
                        onClick = { isRevealed = true },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("tap_to_scratch_button")
                    ) {
                        Text(
                            text = "Tap to Reveal ₹${"%.0f".format(reward.amount)}",
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

@Composable
private fun ConfettiParticles(isRevealed: Boolean) {
    if (!isRevealed) return

    val transition = rememberInfiniteTransition(label = "confetti")
    val animOffset by transition.animateFloat(
        initialValue = -50f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleY"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val colors = listOf(
            Color(0xFFFFD700), Color(0xFF10B981), Color(0xFF3B82F6),
            Color(0xFFEF4444), Color(0xFFA855F7), Color(0xFFF97316)
        )
        val rand = Random(42)
        for (i in 0 until 40) {
            val startX = rand.nextFloat() * size.width
            val speed = 0.5f + rand.nextFloat() * 1.5f
            val currentY = (animOffset * speed + rand.nextFloat() * 200) % size.height
            val color = colors[i % colors.size]
            drawCircle(
                color = color,
                radius = 5.dp.toPx(),
                center = Offset(startX, currentY)
            )
        }
    }
}
