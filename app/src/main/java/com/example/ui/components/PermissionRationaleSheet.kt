package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Navy900
import com.example.ui.theme.RoyalBlue100
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.SurfaceWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsManagerSheet(
    cameraGranted: Boolean,
    locationGranted: Boolean,
    onRequestCamera: () -> Unit,
    onRequestLocation: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(RoyalBlue100),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Security",
                        tint = RoyalBlue600,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = "PayEase System Access",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Navy900
                    )
                    Text(
                        text = "Required for secure UPI operations",
                        fontSize = 13.sp,
                        color = Slate500
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Camera Permission Item
            PermissionItemCard(
                icon = Icons.Default.CameraAlt,
                title = "Camera Access",
                description = "Scan store QR codes, personal QR, and bills instantly.",
                isGranted = cameraGranted,
                onGrantClick = onRequestCamera
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Location Permission Item
            PermissionItemCard(
                icon = Icons.Default.LocationOn,
                title = "Location Tagging & Security",
                description = "Required by NPCI for fraud detection and tagging payment locations.",
                isGranted = locationGranted,
                onGrantClick = onRequestLocation
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Photos / Storage explanation (Zero-permission Photo Picker)
            PermissionItemCard(
                icon = Icons.Default.PhotoLibrary,
                title = "Receipts & Attachments",
                description = "Privacy-friendly Android Photo Picker for sharing receipts in chat.",
                isGranted = true,
                isAlwaysReady = true,
                onGrantClick = {}
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("permissions_done_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600)
            ) {
                Text(
                    text = "Continue to PayEase",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SurfaceWhite
                )
            }
        }
    }
}

@Composable
private fun PermissionItemCard(
    icon: ImageVector,
    title: String,
    description: String,
    isGranted: Boolean,
    isAlwaysReady: Boolean = false,
    onGrantClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF8FAFC))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (isGranted) Color(0xFFE8F5E9) else Color(0xFFF1F5F9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isGranted) Emerald600 else Slate500,
                modifier = Modifier.size(22.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Navy900
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = Slate500,
                lineHeight = 16.sp
            )
        }

        if (isGranted || isAlwaysReady) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Active",
                    tint = Emerald600,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Active",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Emerald600
                )
            }
        } else {
            Button(
                onClick = onGrantClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                modifier = Modifier.testTag("grant_${title.take(6).lowercase()}")
            ) {
                Text(
                    text = "Allow",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
