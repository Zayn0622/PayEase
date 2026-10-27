package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material.ripple.rememberRipple
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Crimson600
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Navy900
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.SurfaceWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpiPinBottomSheet(
    title: String = "Enter 4-Digit UPI PIN",
    subtitle: String = "HDFC Bank •••• 4821",
    amountToAuthorize: Double? = null,
    errorMessage: String? = null,
    onPinEntered: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var pin by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFCBD5E1))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // NPCI / Security Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Security",
                    tint = RoyalBlue600,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "UPI 256-BIT ENCRYPTED",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate700,
                    letterSpacing = 0.8.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Navy900
            )

            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = Slate500,
                modifier = Modifier.padding(top = 4.dp)
            )

            if (amountToAuthorize != null) {
                Text(
                    text = "₹${"%.2f".format(amountToAuthorize)}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Emerald600,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4 Pin Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 4) {
                    val isFilled = i < pin.length
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(
                                if (isFilled) RoyalBlue600 else Color.Transparent
                            )
                            .border(
                                width = 2.dp,
                                color = if (isFilled) RoyalBlue600 else Slate400,
                                shape = CircleShape
                            )
                    )
                }
            }

            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = errorMessage ?: "",
                    color = Crimson600,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Numeric Keypad
            val rows = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("del", "0", "ok")
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rows.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        row.forEach { key ->
                            KeypadButton(
                                key = key,
                                onClick = {
                                    when (key) {
                                        "del" -> {
                                            if (pin.isNotEmpty()) pin = pin.dropLast(1)
                                        }
                                        "ok" -> {
                                            if (pin.length == 4) onPinEntered(pin)
                                        }
                                        else -> {
                                            if (pin.length < 4) {
                                                pin += key
                                                if (pin.length == 4) {
                                                    onPinEntered(pin)
                                                }
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    key: String,
    onClick: () -> Unit
) {
    val isAction = key == "del" || key == "ok"

    Box(
        modifier = Modifier
            .size(width = 90.dp, height = 54.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (key == "ok") Emerald600 else if (key == "del") Color(0xFFF1F5F9) else Color(0xFFF8FAFC))
            .clickable(onClick = onClick)
            .testTag("keypad_$key"),
        contentAlignment = Alignment.Center
    ) {
        when (key) {
            "del" -> {
                Icon(
                    imageVector = Icons.Default.Backspace,
                    contentDescription = "Delete",
                    tint = Slate700,
                    modifier = Modifier.size(20.dp)
                )
            }
            "ok" -> {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Submit",
                    tint = SurfaceWhite,
                    modifier = Modifier.size(22.dp)
                )
            }
            else -> {
                Text(
                    text = key,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Navy900
                )
            }
        }
    }
}
