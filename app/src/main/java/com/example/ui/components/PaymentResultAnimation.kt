package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Crimson600
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Navy900
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import java.util.Locale
import kotlin.math.roundToInt

enum class PaymentResultStatus {
    PROCESSING,
    SUCCESS,
    FAILED;

    companion object {
        fun fromString(value: String): PaymentResultStatus {
            return when (value.lowercase(Locale.ROOT).trim()) {
                "processing" -> PROCESSING
                "success" -> SUCCESS
                "failed", "failure" -> FAILED
                else -> PROCESSING
            }
        }
    }
}

/**
 * Checks if the user or device has requested reduced motion / disabled animations.
 */
fun isReducedMotionPreferred(context: Context): Boolean {
    return try {
        val durationScale = Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1.0f
        )
        durationScale == 0f
    } catch (_: Exception) {
        false
    }
}

/**
 * Triggers native haptic feedback patterns on mobile device.
 * Success: 50ms light impact
 * Failure: Distinct double-pulse error pattern
 */
fun triggerResultHaptic(context: Context, status: PaymentResultStatus) {
    try {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

        if (vibrator?.hasVibrator() == true) {
            when (status) {
                PaymentResultStatus.SUCCESS -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(50)
                    }
                }
                PaymentResultStatus.FAILED -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 60, 70, 60), -1))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(longArrayOf(0, 60, 70, 60), -1)
                    }
                }
                PaymentResultStatus.PROCESSING -> {}
            }
        }
    } catch (_: Exception) {
        // Fallback silently if device lacks vibrator permissions or hardware
    }
}

/**
 * Self-contained, highly polished payment result animation component.
 * Supports 3 states:
 * - PROCESSING: Smooth rotating circular spinner arc while in-flight.
 * - SUCCESS: Spinner morphs into green circle, checkmark dynamically draws using
 *            path trimming (stroke-dasharray style), snappy scale bounce (0.85 -> 1.05 -> 1.0),
 *            fades in success text & amount, and triggers a 50ms haptic.
 * - FAILED: Spinner morphs into red circle, cross (X) draws itself, horizontal wobble shake
 *           (2-3 quick wobbles), error haptic pattern, and fades in error message & "Try Again".
 * - Respects prefers-reduced-motion by skipping animations and presenting static icon & text.
 */
@Composable
fun PaymentResultAnimation(
    status: PaymentResultStatus,
    amount: Double? = null,
    recipientName: String? = null,
    bankName: String? = null,
    errorMessage: String? = null,
    iconSize: Dp = 104.dp,
    modifier: Modifier = Modifier,
    onTryAgain: (() -> Unit)? = null,
    onDone: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val reducedMotion = remember { isReducedMotionPreferred(context) }

    // Spinner continuous rotation during PROCESSING
    val infiniteTransition = rememberInfiniteTransition(label = "SpinnerRotation")
    val spinnerAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SpinnerAngle"
    )

    // Morphing background and stroke colors
    val targetCircleColor = when (status) {
        PaymentResultStatus.PROCESSING -> RoyalBlue600
        PaymentResultStatus.SUCCESS -> Emerald500
        PaymentResultStatus.FAILED -> Crimson600
    }

    val animatedCircleColor by animateColorAsState(
        targetValue = targetCircleColor,
        animationSpec = tween(if (reducedMotion) 0 else 400),
        label = "CircleColor"
    )

    // Checkmark / Cross path stroke drawing progress (0f -> 1f)
    val drawProgress = remember { Animatable(if (reducedMotion) 1f else 0f) }

    // Snappy bounce scale for Success: 0.85 -> 1.05 -> 1.0
    val scaleBounce = remember { Animatable(1f) }

    // Horizontal wobble shake offset for Failure
    val shakeOffset = remember { Animatable(0f) }

    // Text fade-in state
    var showTextDetails by remember { mutableStateOf(reducedMotion) }

    LaunchedEffect(status) {
        if (reducedMotion) {
            drawProgress.snapTo(1f)
            scaleBounce.snapTo(1f)
            shakeOffset.snapTo(0f)
            showTextDetails = true
            triggerResultHaptic(context, status)
            return@LaunchedEffect
        }

        when (status) {
            PaymentResultStatus.PROCESSING -> {
                drawProgress.snapTo(0f)
                scaleBounce.snapTo(1f)
                shakeOffset.snapTo(0f)
                showTextDetails = false
            }

            PaymentResultStatus.SUCCESS -> {
                drawProgress.snapTo(0f)
                scaleBounce.snapTo(0.85f)
                shakeOffset.snapTo(0f)
                showTextDetails = false

                // 1. Draw checkmark from start to end (550ms, ease-out)
                drawProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing)
                )

                // 2. Right after drawing finishes, snappy scale bounce (0.85 -> 1.05 -> 1.0, ~300ms)
                scaleBounce.animateTo(
                    targetValue = 1.0f,
                    animationSpec = keyframes {
                        durationMillis = 320
                        0.85f at 0
                        1.08f at 160 using FastOutSlowInEasing
                        1.00f at 320 using FastOutSlowInEasing
                    }
                )

                // 3. Trigger 50ms success haptic
                triggerResultHaptic(context, PaymentResultStatus.SUCCESS)

                // 4. Fade in text & amount
                showTextDetails = true
            }

            PaymentResultStatus.FAILED -> {
                drawProgress.snapTo(0f)
                scaleBounce.snapTo(1f)
                shakeOffset.snapTo(0f)
                showTextDetails = false

                // 1. Draw cross (X) lines (550ms, ease-out)
                drawProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing)
                )

                // 2. Right after drawing finishes, horizontal shake (2-3 quick wobbles, ~300ms)
                shakeOffset.animateTo(
                    targetValue = 0f,
                    animationSpec = keyframes {
                        durationMillis = 320
                        0f at 0
                        -14f at 50 using FastOutSlowInEasing
                        14f at 110 using FastOutSlowInEasing
                        -10f at 170 using FastOutSlowInEasing
                        10f at 230 using FastOutSlowInEasing
                        -4f at 280 using FastOutSlowInEasing
                        0f at 320 using FastOutSlowInEasing
                    }
                )

                // 3. Trigger error haptic pattern
                triggerResultHaptic(context, PaymentResultStatus.FAILED)

                // 4. Fade in error details & Try Again button
                showTextDetails = true
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Main Animated Icon Container
        Box(
            modifier = Modifier
                .offset { IntOffset(x = shakeOffset.value.dp.roundToPx(), y = 0) }
                .scale(scaleBounce.value)
                .size(iconSize)
                .testTag("payment_result_icon"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(iconSize)) {
                val strokeWidth = size.minDimension * 0.08f
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = (size.minDimension - strokeWidth) / 2f

                when (status) {
                    PaymentResultStatus.PROCESSING -> {
                        // Background track
                        drawCircle(
                            color = Slate200,
                            radius = radius,
                            center = center,
                            style = Stroke(width = strokeWidth)
                        )

                        // Smooth rotating arc spinner
                        drawArc(
                            color = animatedCircleColor,
                            startAngle = spinnerAngle,
                            sweepAngle = 260f,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(radius * 2f, radius * 2f),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // Inner soft pulse circle
                        drawCircle(
                            color = animatedCircleColor.copy(alpha = 0.12f),
                            radius = radius * 0.72f,
                            center = center
                        )
                    }

                    PaymentResultStatus.SUCCESS -> {
                        // Full Green Circle Background
                        drawCircle(
                            color = animatedCircleColor,
                            radius = radius + (strokeWidth * 0.5f),
                            center = center
                        )

                        // Outer subtle glow ring
                        drawCircle(
                            color = animatedCircleColor.copy(alpha = 0.22f),
                            radius = radius + (strokeWidth * 1.6f),
                            center = center,
                            style = Stroke(width = strokeWidth * 0.7f)
                        )

                        // Checkmark SVG-style line drawing with PathMeasure
                        if (drawProgress.value > 0f) {
                            val checkPath = Path().apply {
                                moveTo(center.x - radius * 0.44f, center.y + radius * 0.02f)
                                lineTo(center.x - radius * 0.10f, center.y + radius * 0.36f)
                                lineTo(center.x + radius * 0.44f, center.y - radius * 0.32f)
                            }

                            val pathMeasure = PathMeasure()
                            pathMeasure.setPath(checkPath, false)
                            val totalLength = pathMeasure.length
                            val animatedPath = Path()
                            pathMeasure.getSegment(
                                startDistance = 0f,
                                stopDistance = totalLength * drawProgress.value,
                                destination = animatedPath,
                                startWithMoveTo = true
                            )

                            drawPath(
                                path = animatedPath,
                                color = Color.White,
                                style = Stroke(
                                    width = strokeWidth * 1.35f,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }

                    PaymentResultStatus.FAILED -> {
                        // Full Red Circle Background
                        drawCircle(
                            color = animatedCircleColor,
                            radius = radius + (strokeWidth * 0.5f),
                            center = center
                        )

                        // Outer subtle glow ring
                        drawCircle(
                            color = animatedCircleColor.copy(alpha = 0.22f),
                            radius = radius + (strokeWidth * 1.6f),
                            center = center,
                            style = Stroke(width = strokeWidth * 0.7f)
                        )

                        // Cross (X) SVG-style line drawing with PathMeasure
                        if (drawProgress.value > 0f) {
                            val crossOffset = radius * 0.36f
                            val crossPath = Path().apply {
                                // Diagonal 1: top-left to bottom-right
                                moveTo(center.x - crossOffset, center.y - crossOffset)
                                lineTo(center.x + crossOffset, center.y + crossOffset)
                                // Diagonal 2: top-right to bottom-left
                                moveTo(center.x + crossOffset, center.y - crossOffset)
                                lineTo(center.x - crossOffset, center.y + crossOffset)
                            }

                            val pathMeasure = PathMeasure()
                            pathMeasure.setPath(crossPath, false)
                            val totalLength = pathMeasure.length
                            val animatedPath = Path()
                            pathMeasure.getSegment(
                                startDistance = 0f,
                                stopDistance = totalLength * drawProgress.value,
                                destination = animatedPath,
                                startWithMoveTo = true
                            )

                            drawPath(
                                path = animatedPath,
                                color = Color.White,
                                style = Stroke(
                                    width = strokeWidth * 1.35f,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // State-Dependent Text and Callbacks
        when (status) {
            PaymentResultStatus.PROCESSING -> {
                Text(
                    text = "Processing Payment...",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
                Spacer(modifier = Modifier.height(6.dp))
                if (amount != null) {
                    Text(
                        text = "₹${"%.2f".format(amount)}${if (!recipientName.isNullOrBlank()) " to $recipientName" else ""}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = RoyalBlue600
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (!bankName.isNullOrBlank()) "Contacting $bankName & NPCI servers..." else "Authorizing with banking switch...",
                    fontSize = 12.sp,
                    color = Slate500,
                    textAlign = TextAlign.Center
                )
            }

            PaymentResultStatus.SUCCESS -> {
                AnimatedVisibility(
                    visible = showTextDetails,
                    enter = fadeIn(animationSpec = tween(if (reducedMotion) 0 else 300))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Payment Successful",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Emerald600,
                            modifier = Modifier.testTag("payment_success_title")
                        )

                        if (amount != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "₹${"%.2f".format(amount)}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = Navy900,
                                modifier = Modifier.testTag("payment_success_amount")
                            )
                        }

                        if (!recipientName.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Paid to $recipientName",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Slate700
                            )
                        }

                        if (!bankName.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Debited from $bankName",
                                fontSize = 12.sp,
                                color = Slate500
                            )
                        }

                        if (onDone != null) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = onDone,
                                colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .height(48.dp)
                                    .testTag("payment_success_done_button")
                            ) {
                                Text(
                                    text = "Done",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            PaymentResultStatus.FAILED -> {
                AnimatedVisibility(
                    visible = showTextDetails,
                    enter = fadeIn(animationSpec = tween(if (reducedMotion) 0 else 300))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Payment Failed",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Crimson600,
                            modifier = Modifier.testTag("payment_failed_title")
                        )

                        if (amount != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "₹${"%.2f".format(amount)}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Navy900
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: "Bank server response timeout (NPCI error 91). Transaction declined by issuing bank.",
                            fontSize = 13.sp,
                            color = Slate700,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            if (onTryAgain != null) {
                                Button(
                                    onClick = onTryAgain,
                                    colors = ButtonDefaults.buttonColors(containerColor = Crimson600),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("payment_failed_retry_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Retry",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Try Again",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                }
                            }

                            if (onDone != null) {
                                OutlinedButton(
                                    onClick = onDone,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("payment_failed_cancel_button")
                                ) {
                                    Text(
                                        text = "Cancel",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = Slate700
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentResultAnimation(
    status: String,
    amount: Double? = null,
    recipientName: String? = null,
    bankName: String? = null,
    errorMessage: String? = null,
    iconSize: Dp = 104.dp,
    modifier: Modifier = Modifier,
    onTryAgain: (() -> Unit)? = null,
    onDone: (() -> Unit)? = null
) {
    PaymentResultAnimation(
        status = PaymentResultStatus.fromString(status),
        amount = amount,
        recipientName = recipientName,
        bankName = bankName,
        errorMessage = errorMessage,
        iconSize = iconSize,
        modifier = modifier,
        onTryAgain = onTryAgain,
        onDone = onDone
    )
}

/**
 * Drop-in full-screen or modal dialog wrapper for PaymentResultAnimation.
 * Enables 1-line integration into any payment checkout flow.
 */
@Composable
fun PaymentResultDialog(
    status: PaymentResultStatus,
    amount: Double? = null,
    recipientName: String? = null,
    bankName: String? = null,
    errorMessage: String? = null,
    onTryAgain: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = {
            if (status != PaymentResultStatus.PROCESSING) {
                onDismiss()
            }
        },
        properties = androidx.compose.ui.window.DialogProperties(
            dismissOnBackPress = status != PaymentResultStatus.PROCESSING,
            dismissOnClickOutside = status != PaymentResultStatus.PROCESSING,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(com.example.ui.theme.SurfaceWhite)
                .padding(vertical = 20.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            PaymentResultAnimation(
                status = status,
                amount = amount,
                recipientName = recipientName,
                bankName = bankName,
                errorMessage = errorMessage,
                onTryAgain = onTryAgain,
                onDone = onDismiss
            )
        }
    }
}
