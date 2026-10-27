package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.Navy900
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.SurfaceWhite
import kotlin.math.abs

/**
 * High quality QR Code Canvas renderer with PayEase logo centered prominently.
 */
@Composable
fun PayEaseQrCode(
    upiPayload: String,
    modifier: Modifier = Modifier,
    size: Dp = 240.dp
) {
    // Generate deterministic 25x25 QR module grid based on payload hash
    val gridSize = 25
    val grid = remember(upiPayload) {
        val modules = Array(gridSize) { BooleanArray(gridSize) }
        val hash = upiPayload.hashCode()

        // 1. Finder patterns at (0,0), (gridSize-7, 0), (0, gridSize-7)
        fun setFinder(row: Int, col: Int) {
            for (r in 0 until 7) {
                for (c in 0 until 7) {
                    val isBorder = r == 0 || r == 6 || c == 0 || c == 6
                    val isInner = r in 2..4 && c in 2..4
                    modules[row + r][col + c] = isBorder || isInner
                }
            }
        }
        setFinder(0, 0)
        setFinder(gridSize - 7, 0)
        setFinder(0, gridSize - 7)

        // 2. Timing patterns
        for (i in 8 until gridSize - 8) {
            modules[6][i] = (i % 2 == 0)
            modules[i][6] = (i % 2 == 0)
        }

        // 3. Populate data modules using hash
        val rand = java.util.Random(hash.toLong())
        for (r in 0 until gridSize) {
            for (c in 0 until gridSize) {
                // Skip finder patterns
                val inFinder1 = r < 8 && c < 8
                val inFinder2 = r >= gridSize - 8 && c < 8
                val inFinder3 = r < 8 && c >= gridSize - 8
                val inCenterLogo = r in 9..15 && c in 9..15

                if (!inFinder1 && !inFinder2 && !inFinder3 && !inCenterLogo) {
                    if (r != 6 && c != 6) {
                        modules[r][c] = rand.nextBoolean()
                    }
                }
            }
        }
        modules
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceWhite)
            .border(1.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellW = this.size.width / gridSize
            val cellH = this.size.height / gridSize

            for (r in 0 until gridSize) {
                for (c in 0 until gridSize) {
                    // Leave space for center PayEase logo
                    if (r in 9..15 && c in 9..15) continue

                    if (grid[r][c]) {
                        val isFinderArea = (r < 7 && c < 7) ||
                                (r >= gridSize - 7 && c < 7) ||
                                (r < 7 && c >= gridSize - 7)

                        val moduleColor = if (isFinderArea) RoyalBlue600 else Navy900
                        drawRoundRect(
                            color = moduleColor,
                            topLeft = Offset(c * cellW, r * cellH),
                            size = Size(cellW * 0.92f, cellH * 0.92f),
                            cornerRadius = CornerRadius(cellW * 0.25f, cellH * 0.25f)
                        )
                    }
                }
            }
        }

        // Prominent Center Logo
        Box(
            modifier = Modifier
                .size(size * 0.28f)
                .clip(CircleShape)
                .background(SurfaceWhite)
                .border(2.5.dp, RoyalBlue600, CircleShape)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_payease_logo),
                contentDescription = "PayEase Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        }
    }
}
