package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.security.SecurityPreferences
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Navy900
import com.example.ui.theme.RoyalBlue500
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.SurfaceWhite
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SetDailyLimitDialog(
    currentLimit: Double,
    isLimitEnabled: Boolean,
    todaySpent: Double,
    onSaveLimit: (Double) -> Unit,
    onToggleLimit: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val indianFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }

    var limitInputText by remember {
        mutableStateOf(currentLimit.toInt().toString())
    }
    var enabledState by remember { mutableStateOf(isLimitEnabled) }
    var inputError by remember { mutableStateOf<String?>(null) }

    val presetLimits = listOf(
        5000.0 to "Strict",
        10000.0 to "Essentials",
        25000.0 to "Recommended",
        50000.0 to "Flexible",
        100000.0 to "UPI Max"
    )

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
                            text = "Daily Transaction Limit",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )
                        Text(
                            text = "Financial Security & Spending Control",
                            fontSize = 12.sp,
                            color = Slate500
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_daily_limit_dialog_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = Slate600
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Enable/Disable Switch Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Enforce Daily Spending Cap",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Navy900
                        )
                        Text(
                            text = "Guard against fraud and overspending",
                            fontSize = 12.sp,
                            color = Slate500
                        )
                    }

                    Switch(
                        checked = enabledState,
                        onCheckedChange = { checked ->
                            enabledState = checked
                            onToggleLimit(checked)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SurfaceWhite,
                            checkedTrackColor = RoyalBlue600
                        ),
                        modifier = Modifier.testTag("toggle_daily_limit_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "CONFIGURED DAILY LIMIT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Slate400,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Custom Limit Input Field
            OutlinedTextField(
                value = limitInputText,
                onValueChange = { input ->
                    val filtered = input.filter { it.isDigit() }
                    limitInputText = filtered
                    inputError = when {
                        filtered.isBlank() -> "Please enter an amount"
                        (filtered.toDoubleOrNull() ?: 0.0) < SecurityPreferences.MIN_DAILY_LIMIT ->
                            "Minimum daily limit is ₹${SecurityPreferences.MIN_DAILY_LIMIT.toInt()}"
                        (filtered.toDoubleOrNull() ?: 0.0) > SecurityPreferences.MAX_DAILY_LIMIT ->
                            "Maximum daily limit is ₹${SecurityPreferences.MAX_DAILY_LIMIT.toInt()}"
                        else -> null
                    }
                },
                leadingIcon = {
                    Text(
                        text = "₹",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Navy900,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                },
                label = { Text("Daily Limit Amount") },
                supportingText = {
                    if (inputError != null) {
                        Text(text = inputError!!, color = Color(0xFFDC2626), fontSize = 11.sp)
                    } else {
                        Text(text = "Valid range: ₹500 to ₹1,00,000", color = Slate500, fontSize = 11.sp)
                    }
                },
                isError = inputError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RoyalBlue600,
                    unfocusedBorderColor = Color(0xFFCBD5E1),
                    focusedContainerColor = SurfaceWhite,
                    unfocusedContainerColor = SurfaceWhite
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_limit_input_field")
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "QUICK PRESETS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Slate400,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Preset Chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                presetLimits.forEach { (amount, label) ->
                    val isSelected = limitInputText == amount.toInt().toString()
                    val formatted = indianFormat.format(amount).replace(".00", "")

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color(0xFFEEF2FF) else Color(0xFFF1F5F9))
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) RoyalBlue600 else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                limitInputText = amount.toInt().toString()
                                inputError = null
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("preset_limit_${amount.toInt()}")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = formatted,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (isSelected) RoyalBlue600 else Navy900
                            )
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                color = if (isSelected) RoyalBlue500 else Slate500
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Today's Spent Context
            val parsedLimit = limitInputText.toDoubleOrNull() ?: currentLimit
            val remainingNew = (parsedLimit - todaySpent).coerceAtLeast(0.0)

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFBBF7D0), RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        tint = Emerald600,
                        modifier = Modifier.size(20.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Today's Spent: ${indianFormat.format(todaySpent)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )
                        Text(
                            text = "Remaining allowance with this limit: ${indianFormat.format(remainingNew)}",
                            fontSize = 11.sp,
                            color = Slate600
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("cancel_daily_limit_button")
                ) {
                    Text(text = "Cancel", color = Slate600, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        val amount = limitInputText.toDoubleOrNull()
                        if (amount != null && amount >= SecurityPreferences.MIN_DAILY_LIMIT && amount <= SecurityPreferences.MAX_DAILY_LIMIT) {
                            onSaveLimit(amount)
                        }
                    },
                    enabled = inputError == null && limitInputText.isNotBlank(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                    modifier = Modifier
                        .weight(1.5f)
                        .height(48.dp)
                        .testTag("save_daily_limit_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Security,
                        contentDescription = null,
                        tint = SurfaceWhite,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save Daily Limit",
                        fontWeight = FontWeight.Bold,
                        color = SurfaceWhite
                    )
                }
            }
        }
    }
}
