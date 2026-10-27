package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.RewardEntity
import com.example.ui.PayEaseViewModel
import com.example.ui.components.CashbackCelebrationDialog
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Navy900
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.SurfaceWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(
    viewModel: PayEaseViewModel,
    onNavigateBack: () -> Unit
) {
    val rewards by viewModel.rewards.collectAsState()
    val totalWon by viewModel.totalCashbackWon.collectAsState()
    val unclaimedCount by viewModel.unclaimedRewardsCount.collectAsState()

    var selectedRewardForScratch by remember { mutableStateOf<RewardEntity?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "PayEase Rewards Club",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Navy900
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("rewards_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Navy900
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = SurfaceWhite)
            )
        },
        containerColor = Slate50
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Header Hero Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .shadow(4.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFFFFBEB), Color(0xFFFEF3C7))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Celebration,
                                    contentDescription = null,
                                    tint = Amber500,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "TOTAL CASHBACK EARNED",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E),
                                    letterSpacing = 0.8.sp
                                )
                            }

                            Text(
                                text = "₹${"%.2f".format(totalWon ?: 0.0)}",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                color = Navy900,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            Text(
                                text = "$unclaimedCount scratch cards waiting to be revealed!",
                                fontSize = 12.sp,
                                color = Slate700,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFDE68A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Redeem,
                                contentDescription = null,
                                tint = Amber500,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }

            Text(
                text = "Your Scratch Cards",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Navy900,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(rewards, key = { it.id }) { reward ->
                    ScratchCardTile(
                        reward = reward,
                        onClick = {
                            selectedRewardForScratch = reward
                        }
                    )
                }
            }
        }
    }

    // Modal Scratch Dialog if user taps any card
    selectedRewardForScratch?.let { reward ->
        CashbackCelebrationDialog(
            reward = reward,
            onClaimReward = { amt ->
                viewModel.claimCelebrationReward(amt)
            },
            onDismiss = {
                selectedRewardForScratch = null
            }
        )
    }
}

@Composable
private fun ScratchCardTile(
    reward: RewardEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .shadow(3.dp, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("reward_tile_${reward.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        if (!reward.isScratched) {
            // Unscratched mysterious card
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(RoyalBlue600, Color(0xFF1E3A8A))
                        )
                    )
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = Color(0xFFFDE047),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "SCRATCH CARD",
                        color = SurfaceWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "Tap to open",
                        color = Color(0xFFDBEAFE),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        } else {
            // Scratched revealed card
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFFBEB))
                    .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(16.dp))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Emerald600,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Won",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate500
                        )
                    }

                    Text(
                        text = "₹${"%.0f".format(reward.amount)}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Emerald600
                    )

                    Text(
                        text = reward.title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Slate700,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
