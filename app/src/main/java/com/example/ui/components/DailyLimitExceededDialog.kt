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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DailyLimitExceededInfo
import com.example.ui.theme.Amber500
import com.example.ui.theme.Crimson600
import com.example.ui.theme.Navy900
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.SurfaceWhite
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DailyLimitExceededDialog(
    info: DailyLimitExceededInfo,
    onAdjustLimit: () -> Unit,
    onDismiss: () -> Unit
) {
    val indianFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(24.dp),
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Warning Icon
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEF2F2))
                ) {
                    Icon(
                        imageVector = Icons.Filled.GppBad,
                        contentDescription = "Limit Exceeded",
                        tint = Crimson600,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Daily Limit Exceeded",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Navy900,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "This payment of ${indianFormat.format(info.attemptedAmount)} to ${info.recipientName} cannot be completed because it exceeds your daily spending limit.",
                    fontSize = 13.sp,
                    color = Slate600,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Breakdown Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BreakdownRow(
                            label = "Attempted Payment",
                            value = indianFormat.format(info.attemptedAmount),
                            valueColor = Navy900,
                            isBold = true
                        )

                        BreakdownRow(
                            label = "Spent Today",
                            value = indianFormat.format(info.todaySpent),
                            valueColor = Slate600
                        )

                        HorizontalDivider(color = Color(0xFFE2E8F0))

                        BreakdownRow(
                            label = "Configured Daily Limit",
                            value = indianFormat.format(info.dailyLimit),
                            valueColor = RoyalBlue600,
                            isBold = true
                        )

                        BreakdownRow(
                            label = "Over Limit By",
                            value = "+${indianFormat.format(info.excessAmount)}",
                            valueColor = Crimson600,
                            isBold = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "You can increase your daily limit to proceed with this payment immediately.",
                    fontSize = 11.sp,
                    color = Slate500,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onAdjustLimit,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("adjust_limit_from_exceeded_dialog_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = null,
                    tint = SurfaceWhite,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Adjust Daily Limit",
                    fontWeight = FontWeight.Bold,
                    color = SurfaceWhite
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("dismiss_limit_exceeded_dialog_button")
            ) {
                Text(
                    text = "Cancel Payment",
                    color = Slate600,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}

@Composable
private fun BreakdownRow(
    label: String,
    value: String,
    valueColor: Color,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Slate500
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = valueColor
        )
    }
}
