package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entity.BankAccountEntity
import com.example.ui.PendingPaymentAuth
import com.example.ui.theme.Amber500
import com.example.ui.theme.Crimson600
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Navy900
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.SurfaceWhite
import kotlin.math.roundToInt

data class InsufficientBalanceData(
    val attemptedAmount: Double,
    val availableBalance: Double,
    val deficit: Double,
    val bankAccount: BankAccountEntity,
    val recipientName: String,
    val pendingPayment: PendingPaymentAuth
)

@Composable
fun InsufficientBalanceDialog(
    data: InsufficientBalanceData,
    allBanks: List<BankAccountEntity>,
    onSelectDifferentBankAndPay: (BankAccountEntity) -> Unit,
    onAddFundsAndRetry: (amountToAdd: Double) -> Unit,
    onDismiss: () -> Unit
) {
    val scaleAnim = remember { Animatable(0.2f) }
    val bounceY = remember { Animatable(0f) }
    val rippleScale = remember { Animatable(0.8f) }
    val rippleAlpha = remember { Animatable(0.7f) }

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.65f, stiffness = 400f)
        )
    }

    // Bouncing wallet animation
    LaunchedEffect(Unit) {
        while (true) {
            bounceY.animateTo(-12f, animationSpec = tween(400, easing = FastOutSlowInEasing))
            bounceY.animateTo(0f, animationSpec = spring(dampingRatio = 0.5f))
            bounceY.animateTo(-6f, animationSpec = tween(250, easing = FastOutSlowInEasing))
            bounceY.animateTo(0f, animationSpec = spring(dampingRatio = 0.5f))
            kotlinx.coroutines.delay(1200)
        }
    }

    // Amber pulse ripple
    LaunchedEffect(Unit) {
        while (true) {
            rippleScale.snapTo(0.8f)
            rippleAlpha.snapTo(0.6f)
            rippleScale.animateTo(1.5f, animationSpec = tween(1300, easing = FastOutSlowInEasing))
            rippleAlpha.animateTo(0f, animationSpec = tween(1300, easing = FastOutSlowInEasing))
        }
    }

    // Other banks that have sufficient balance
    val sufficientBanks = remember(allBanks, data.attemptedAmount) {
        allBanks.filter { it.id != data.bankAccount.id && it.balance >= data.attemptedAmount }
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
                    .clip(RoundedCornerShape(28.dp))
                    .testTag("insufficient_balance_dialog"),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated Wallet / Warning Badge
                    Box(
                        modifier = Modifier.size(86.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Amber ripple
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .scale(rippleScale.value)
                                .clip(CircleShape)
                                .background(Amber500.copy(alpha = rippleAlpha.value))
                        )

                        // Wallet badge
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .offset { IntOffset(0, bounceY.value.roundToInt()) }
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFFF59E0B), Amber500)
                                    )
                                )
                                .border(3.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = "Insufficient Balance",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Insufficient Balance",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Navy900
                    )

                    Text(
                        text = "Your account does not have enough funds to complete this payment",
                        fontSize = 13.sp,
                        color = Slate500,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    // Breakdown Card
                    Surface(
                        color = Color(0xFFFFFBEB),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
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
                                Text("Payment Amount", fontSize = 12.sp, color = Slate700)
                                Text(
                                    "₹${"%.2f".format(data.attemptedAmount)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Navy900
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Available in ${data.bankAccount.bankName}",
                                    fontSize = 12.sp,
                                    color = Slate700
                                )
                                Text(
                                    "₹${"%.2f".format(data.availableBalance)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Slate700
                                )
                            }

                            HorizontalDivider(color = Color(0xFFFDE68A), thickness = 1.dp)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Shortfall",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Crimson600
                                )
                                Text(
                                    "-₹${"%.2f".format(data.deficit)}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Crimson600
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Other Eligible Banks
                    if (sufficientBanks.isNotEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Pay with another linked account:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Navy900
                            )

                            sufficientBanks.forEach { bank ->
                                Surface(
                                    onClick = { onSelectDifferentBankAndPay(bank) },
                                    color = Color(0xFFF8FAFC),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(Emerald500.copy(alpha = 0.15f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.AccountBalance,
                                                    contentDescription = null,
                                                    tint = Emerald600,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            Column {
                                                Text(
                                                    text = bank.bankName,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Navy900
                                                )
                                                Text(
                                                    text = "Bal: ₹${"%.2f".format(bank.balance)}",
                                                    fontSize = 11.sp,
                                                    color = Emerald600,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }

                                        Button(
                                            onClick = { onSelectDifferentBankAndPay(bank) },
                                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                                horizontal = 12.dp,
                                                vertical = 6.dp
                                            )
                                        ) {
                                            Text("Use", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Quick Deposit / Add Funds Option
                    val depositAmount = (data.deficit + 1000.0).coerceAtLeast(2000.0)
                    Button(
                        onClick = { onAddFundsAndRetry(depositAmount) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("add_funds_and_retry_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Emerald600
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Deposit ₹${depositAmount.toInt()} & Retry Payment",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("dismiss_insufficient_balance_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            fontWeight = FontWeight.Medium,
                            color = Slate700,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
