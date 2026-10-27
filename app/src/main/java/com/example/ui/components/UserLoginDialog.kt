package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.remote.firestore.UserProfile
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Navy900
import com.example.ui.theme.RoyalBlue50
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.RoyalBlue700
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.SurfaceWhite

@Composable
fun UserLoginDialog(
    onDismiss: () -> Unit,
    onLoginSuccess: (UserProfile) -> Unit
) {
    var phoneOrEmail by remember { mutableStateOf("+91 98765 43210") }
    var displayName by remember { mutableStateOf("Alex Morgan") }
    var otpCode by remember { mutableStateOf("7824") }
    var isVerifying by remember { mutableStateOf(false) }

    // Quick Switch presets
    val presetProfiles = listOf(
        UserProfile(
            userId = "user_zain",
            displayName = "Zain Taqi",
            email = "zaintaqi85@gmail.com",
            phoneNumber = "+91 98111 22334",
            vpa = "zain.taqi@payease",
            avatarType = "PRESET_3D_MALE",
            avatarEmoji = "🚀",
            avatarBgColor = 0xFF2563EB
        ),
        UserProfile(
            userId = "user_alex",
            displayName = "Alex Morgan",
            email = "alex.morgan@payease.app",
            phoneNumber = "+91 98765 43210",
            vpa = "alex.morgan@payease",
            avatarType = "PRESET_3D_MALE",
            avatarEmoji = "💼",
            avatarBgColor = 0xFF0D9488
        ),
        UserProfile(
            userId = "user_priya",
            displayName = "Priya Sharma",
            email = "priya.sharma@payease.app",
            phoneNumber = "+91 98765 11223",
            vpa = "priya.sharma@payease",
            avatarType = "PRESET_3D_FEMALE",
            avatarEmoji = "🌸",
            avatarBgColor = 0xFF7C3AED
        )
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(SurfaceWhite)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(RoyalBlue50),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Sign In",
                            tint = RoyalBlue600,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Sign In to PayEase",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Navy900
                        )
                        Text(
                            text = "Secure UPI & Cloud Sync Access",
                            fontSize = 12.sp,
                            color = Slate500
                        )
                    }
                }

                // Quick Switch Profile Chips
                Text(
                    text = "Quick Sign In As:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate700,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    presetProfiles.forEach { preset ->
                        Surface(
                            onClick = {
                                onLoginSuccess(preset)
                            },
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    UserAvatar(profile = preset, size = 36.dp)
                                    Column {
                                        Text(
                                            text = preset.displayName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Navy900
                                        )
                                        Text(
                                            text = preset.email,
                                            fontSize = 11.sp,
                                            color = Slate500
                                        )
                                    }
                                }

                                Text(
                                    text = "Select →",
                                    fontSize = 12.sp,
                                    color = RoyalBlue600,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Or enter custom login credentials:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate700
                )

                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Your Name") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Slate400)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_name_input")
                )

                OutlinedTextField(
                    value = phoneOrEmail,
                    onValueChange = { phoneOrEmail = it },
                    label = { Text("Phone Number or Email") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = Slate400)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_phone_input")
                )

                OutlinedTextField(
                    value = otpCode,
                    onValueChange = { otpCode = it },
                    label = { Text("One-Time Password (OTP)") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Slate400)
                    },
                    trailingIcon = {
                        Text(
                            text = "Auto-fill",
                            color = Emerald600,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { otpCode = "7824" }
                                .padding(end = 12.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_otp_input")
                )

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = {
                        val newProfile = UserProfile(
                            userId = "user_" + System.currentTimeMillis().toString().takeLast(6),
                            displayName = displayName.ifBlank { "PayEase User" },
                            email = if (phoneOrEmail.contains("@")) phoneOrEmail else "$displayName@payease.app".replace(" ", ".").lowercase(),
                            phoneNumber = if (phoneOrEmail.contains("@")) "+91 98765 43210" else phoneOrEmail,
                            vpa = "${displayName.replace(" ", ".").lowercase()}@payease",
                            avatarType = "PRESET_3D_MALE"
                        )
                        onLoginSuccess(newProfile)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("login_submit_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600)
                ) {
                    Text(
                        text = "Verify OTP & Sign In",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel", color = Slate500)
                }
            }
        }
    }
}
