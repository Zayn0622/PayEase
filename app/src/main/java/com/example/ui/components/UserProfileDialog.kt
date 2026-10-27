package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.remote.firestore.UserProfile
import com.example.ui.theme.Crimson600
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AvatarOption(
    val id: String,
    val type: String, // PRESET_3D_MALE, PRESET_3D_FEMALE, PRESET_EMOJI, INITIALS
    val emoji: String = "",
    val bgColor: Long = 0xFF2563EB,
    val label: String
)

@Composable
fun UserProfileDialog(
    profile: UserProfile,
    isLoggedIn: Boolean,
    isSyncing: Boolean,
    syncStatusMessage: String?,
    onDismiss: () -> Unit,
    onSaveProfile: (name: String, vpa: String, phone: String, email: String, avatarType: String, avatarEmoji: String, avatarColor: Long) -> Unit,
    onSyncAll: () -> Unit,
    onLogout: () -> Unit,
    onOpenLogin: () -> Unit
) {
    var displayName by remember(profile) { mutableStateOf(profile.displayName) }
    var vpa by remember(profile) { mutableStateOf(profile.vpa) }
    var phone by remember(profile) { mutableStateOf(profile.phoneNumber) }
    var email by remember(profile) { mutableStateOf(profile.email) }
    var selectedAvatarType by remember(profile) { mutableStateOf(profile.avatarType) }
    var selectedEmoji by remember(profile) { mutableStateOf(profile.avatarEmoji) }
    var selectedBgColor by remember(profile) { mutableStateOf(profile.avatarBgColor) }

    var showLogoutConfirm by remember { mutableStateOf(false) }

    val formattedDate = remember(profile.updatedAt) {
        val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        sdf.format(Date(profile.updatedAt))
    }

    val previewProfile = remember(profile, displayName, selectedAvatarType, selectedEmoji, selectedBgColor) {
        profile.copy(
            displayName = displayName,
            avatarType = selectedAvatarType,
            avatarEmoji = selectedEmoji,
            avatarBgColor = selectedBgColor
        )
    }

    val avatarOptions = remember {
        listOf(
            AvatarOption("3d_male", "PRESET_3D_MALE", "", 0xFF2563EB, "3D Pro"),
            AvatarOption("3d_female", "PRESET_3D_FEMALE", "", 0xFF059669, "3D Star"),
            AvatarOption("rocket", "PRESET_EMOJI", "🚀", 0xFF2563EB, "Astro"),
            AvatarOption("cyber", "PRESET_EMOJI", "⚡", 0xFFE11D48, "Cyber"),
            AvatarOption("fox", "PRESET_EMOJI", "🦊", 0xFFEA580C, "Fox"),
            AvatarOption("pro", "PRESET_EMOJI", "💼", 0xFF0D9488, "Finance"),
            AvatarOption("vip", "PRESET_EMOJI", "👑", 0xFFD97706, "VIP"),
            AvatarOption("creative", "PRESET_EMOJI", "🎨", 0xFF7C3AED, "Artist"),
            AvatarOption("star", "PRESET_EMOJI", "🌟", 0xFFF59E0B, "Gold"),
            AvatarOption("initials_blue", "INITIALS", "", 0xFF2563EB, "Initials")
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                UserAvatar(
                    profile = previewProfile,
                    size = 46.dp,
                    borderWidth = 2.dp,
                    borderColor = RoyalBlue600
                )
                Column {
                    Text(
                        text = if (isLoggedIn) "Account & Profile" else "Guest Account",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Navy900
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isLoggedIn) Emerald500 else Color(0xFFF59E0B))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isLoggedIn) "Firestore Connected • Logged In" else "Logged Out • Local Only",
                            fontSize = 11.sp,
                            color = if (isLoggedIn) Emerald600 else Color(0xFFD97706),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cloud Sync Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Slate100)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isSyncing) Icons.Default.CloudSync else Icons.Default.CloudDone,
                                contentDescription = "Cloud Status",
                                tint = if (isSyncing) RoyalBlue600 else Emerald600,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isSyncing) "Syncing with Firestore..." else "Last synced: $formattedDate",
                                fontSize = 11.sp,
                                color = Slate700
                            )
                        }

                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = RoyalBlue600
                            )
                        }
                    }
                }

                if (!syncStatusMessage.isNullOrBlank()) {
                    Text(
                        text = syncStatusMessage,
                        fontSize = 11.sp,
                        color = RoyalBlue600,
                        fontWeight = FontWeight.Medium
                    )
                }

                // AVATAR PICKER SECTION
                Text(
                    text = "Choose Profile Avatar",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {
                    items(avatarOptions) { opt ->
                        val isSelected = when (opt.type) {
                            "PRESET_3D_MALE" -> selectedAvatarType == "PRESET_3D_MALE"
                            "PRESET_3D_FEMALE" -> selectedAvatarType == "PRESET_3D_FEMALE"
                            "PRESET_EMOJI" -> selectedAvatarType == "PRESET_EMOJI" && selectedEmoji == opt.emoji
                            else -> selectedAvatarType == "INITIALS"
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    selectedAvatarType = opt.type
                                    selectedEmoji = opt.emoji
                                    selectedBgColor = opt.bgColor
                                }
                                .background(if (isSelected) RoyalBlue50 else Color.Transparent)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) RoyalBlue600 else Slate200,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(8.dp)
                        ) {
                            when (opt.type) {
                                "PRESET_3D_MALE" -> {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_avatar_user),
                                        contentDescription = opt.label,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                    )
                                }
                                "PRESET_3D_FEMALE" -> {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_avatar_female),
                                        contentDescription = opt.label,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                    )
                                }
                                "PRESET_EMOJI" -> {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(Color(opt.bgColor)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = opt.emoji, fontSize = 20.sp)
                                    }
                                }
                                else -> {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(Color(opt.bgColor)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = displayName.take(2).uppercase().ifBlank { "AM" },
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = opt.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) RoyalBlue600 else Slate700
                            )
                        }
                    }
                }

                HorizontalDivider(color = Slate200, thickness = 1.dp)

                // Display Name field
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Display Name") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Name",
                            tint = Slate500,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_name_input")
                )

                // UPI VPA field
                OutlinedTextField(
                    value = vpa,
                    onValueChange = { vpa = it },
                    label = { Text("UPI ID (VPA)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "UPI ID",
                            tint = Slate500,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_vpa_input")
                )

                // Mobile number field
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone",
                            tint = Slate500,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_phone_input")
                )

                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            tint = Slate500,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_email_input")
                )

                // KYC Status badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Verified KYC",
                        tint = Emerald600,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "KYC Status: ${profile.kycStatus} • Cloud Sync Enabled",
                        fontSize = 11.sp,
                        color = Emerald600,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Force Sync button
                OutlinedButton(
                    onClick = onSyncAll,
                    enabled = !isSyncing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_sync_all_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudSync,
                        contentDescription = "Sync",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Sync All Data with Firestore", fontSize = 13.sp)
                }

                // LOGIN / LOGOUT OPTION
                HorizontalDivider(color = Slate200, thickness = 1.dp)

                if (isLoggedIn) {
                    OutlinedButton(
                        onClick = { showLogoutConfirm = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_logout_button"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Crimson600
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = Crimson600,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log Out from PayEase", color = Crimson600, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                } else {
                    Button(
                        onClick = onOpenLogin,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_login_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Login,
                            contentDescription = "Sign In",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sign In to Your Account", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveProfile(displayName, vpa, phone, email, selectedAvatarType, selectedEmoji, selectedBgColor)
                },
                enabled = !isSyncing && displayName.isNotBlank() && vpa.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                modifier = Modifier.testTag("profile_save_button")
            ) {
                Text("Save Profile")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("profile_dismiss_button")
            ) {
                Text("Close", color = Slate700)
            }
        }
    )

    // Logout Confirmation Dialog
    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = {
                Text("Log Out of PayEase?", fontWeight = FontWeight.Bold, color = Navy900)
            },
            text = {
                Text(
                    "You will be logged out of this device. Your bank accounts and transactions will remain saved in Firestore.",
                    color = Slate700,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirm = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Crimson600),
                    modifier = Modifier.testTag("confirm_logout_button")
                ) {
                    Text("Yes, Log Out", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text("Cancel", color = Slate700)
                }
            }
        )
    }
}
