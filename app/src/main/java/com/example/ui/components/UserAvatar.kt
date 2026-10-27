package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.remote.firestore.UserProfile
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.RoyalBlue700

@Composable
fun UserAvatar(
    profile: UserProfile,
    size: Dp = 40.dp,
    modifier: Modifier = Modifier,
    borderWidth: Dp = 0.dp,
    borderColor: Color = Color.White
) {
    val baseModifier = modifier
        .size(size)
        .clip(CircleShape)
        .then(if (borderWidth > 0.dp) Modifier.border(borderWidth, borderColor, CircleShape) else Modifier)

    when (profile.avatarType) {
        "PRESET_3D_MALE" -> {
            Image(
                painter = painterResource(id = R.drawable.img_avatar_user),
                contentDescription = "User Avatar",
                contentScale = ContentScale.Crop,
                modifier = baseModifier
            )
        }
        "PRESET_3D_FEMALE" -> {
            Image(
                painter = painterResource(id = R.drawable.img_avatar_female),
                contentDescription = "User Avatar",
                contentScale = ContentScale.Crop,
                modifier = baseModifier
            )
        }
        "PRESET_EMOJI" -> {
            Box(
                modifier = baseModifier.background(Color(profile.avatarBgColor)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profile.avatarEmoji,
                    fontSize = (size.value * 0.48f).sp
                )
            }
        }
        else -> {
            // Default Initials
            Box(
                modifier = baseModifier.background(
                    Brush.linearGradient(
                        listOf(Color(profile.avatarBgColor), RoyalBlue700)
                    )
                ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profile.displayName.take(2).uppercase().ifBlank { "AM" },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = (size.value * 0.4f).sp
                )
            }
        }
    }
}
