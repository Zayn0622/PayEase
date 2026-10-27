package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = RoyalBlue500,
    onPrimary = SurfaceWhite,
    primaryContainer = Navy700,
    onPrimaryContainer = RoyalBlue100,
    secondary = Emerald500,
    onSecondary = SurfaceWhite,
    secondaryContainer = Emerald700,
    onSecondaryContainer = Emerald100,
    tertiary = Amber500,
    background = Navy900,
    onBackground = SurfaceWhite,
    surface = Navy800,
    onSurface = SurfaceWhite,
    surfaceVariant = Navy700,
    onSurfaceVariant = Slate200
  )

private val LightColorScheme =
  lightColorScheme(
    primary = RoyalBlue600,
    onPrimary = SurfaceWhite,
    primaryContainer = RoyalBlue50,
    onPrimaryContainer = RoyalBlue600,
    secondary = Emerald600,
    onSecondary = SurfaceWhite,
    secondaryContainer = Emerald50,
    onSecondaryContainer = Emerald700,
    tertiary = Amber500,
    background = Slate50,
    onBackground = Slate900,
    surface = SurfaceWhite,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate700
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false, // Default to crisp trustworthy light financial theme
  dynamicColor: Boolean = false, // Preserve brand blue and emerald green
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

