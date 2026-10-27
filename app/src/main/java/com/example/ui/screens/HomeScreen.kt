package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.R
import com.example.data.local.entity.ContactEntity
import com.example.ui.PayEaseViewModel
import com.example.ui.PendingPaymentAuth
import com.example.ui.theme.Amber500
import com.example.ui.theme.Crimson600
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Navy900
import com.example.ui.components.UserAvatar
import com.example.ui.components.UserProfileDialog
import com.example.ui.theme.RoyalBlue100
import com.example.ui.theme.RoyalBlue50
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.RoyalBlue700
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.SurfaceWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: PayEaseViewModel,
    onNavigateToScanner: () -> Unit,
    onNavigateToMyQr: () -> Unit,
    onNavigateToBanks: (initialTab: Int) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToRewards: () -> Unit,
    onOpenContactChat: (ContactEntity) -> Unit
) {
    val bankAccounts by viewModel.bankAccounts.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val transactions by viewModel.filteredTransactions.collectAsState()
    val rewards by viewModel.rewards.collectAsState()
    val totalCashbackWon by viewModel.totalCashbackWon.collectAsState()
    val revealedBalances by viewModel.revealedBalanceMap.collectAsState()
    val isDailyLimitEnabled by viewModel.isDailyLimitEnabled.collectAsState()
    val dailyTransactionLimit by viewModel.dailyTransactionLimit.collectAsState()
    val todaySpentAmount by viewModel.todaySpentAmount.collectAsState()
    val dailyLimitProgress by viewModel.dailyLimitProgress.collectAsState()
    val remainingDailyLimit by viewModel.remainingDailyLimit.collectAsState()

    val userProfile by viewModel.userProfile.collectAsState()
    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()
    val isCloudSyncing by viewModel.isCloudSyncing.collectAsState()
    val cloudSyncStatusMessage by viewModel.cloudSyncStatusMessage.collectAsState()
    val showProfileDialog by viewModel.showProfileDialog.collectAsState()

    val primaryBank = bankAccounts.firstOrNull { it.isPrimary } ?: bankAccounts.firstOrNull()

    val context = LocalContext.current
    val activity = context as? FragmentActivity

    // Send Money dialog
    var showSendMoneyDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_payease_logo),
                            contentDescription = "PayEase Logo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceWhite)
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                        )
                        Column {
                            Text(
                                text = "PayEase",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Navy900
                            )
                            Text(
                                text = "UPI & Social Payments",
                                fontSize = 11.sp,
                                color = Slate500
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.openSecuritySettings() },
                        modifier = Modifier.testTag("home_security_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Security Settings",
                            tint = Emerald600
                        )
                    }

                    IconButton(
                        onClick = onNavigateToScanner,
                        modifier = Modifier.testTag("home_scanner_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scan QR",
                            tint = RoyalBlue600
                        )
                    }

                    IconButton(
                        onClick = onNavigateToMyQr,
                        modifier = Modifier.testTag("home_my_qr_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "My QR",
                            tint = RoyalBlue600
                        )
                    }

                    IconButton(
                        onClick = { viewModel.setShowProfileDialog(true) },
                        modifier = Modifier.testTag("home_user_profile_button")
                    ) {
                        UserAvatar(
                            profile = userProfile,
                            size = 34.dp,
                            borderWidth = 1.5.dp,
                            borderColor = if (isUserLoggedIn) RoyalBlue600 else Amber500
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
            )
        },
        containerColor = Slate50
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Logged Out Guest Mode Alert Banner
            if (!isUserLoggedIn) {
                Surface(
                    onClick = { viewModel.openLoginDialog() },
                    color = Color(0xFFFFFBEB),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .testTag("guest_login_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("👋", fontSize = 18.sp)
                            Column {
                                Text(
                                    text = "Browsing in Guest Mode",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Navy900
                                )
                                Text(
                                    text = "Sign in to access your bank accounts and sync across devices",
                                    fontSize = 11.sp,
                                    color = Slate700
                                )
                            }
                        }
                        Button(
                            onClick = { viewModel.openLoginDialog() },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Sign In", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Cloud Sync Status Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF1F5F9))
                    .clickable { viewModel.setShowProfileDialog(true) }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Icon(
                        imageVector = if (isCloudSyncing) Icons.Default.CloudSync else Icons.Default.CloudDone,
                        contentDescription = "Cloud Status",
                        tint = if (isCloudSyncing) RoyalBlue600 else Emerald600,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (isCloudSyncing) "Syncing with Firestore..." else "Cloud Synced • ${userProfile.displayName} (${userProfile.vpa})",
                        fontSize = 11.sp,
                        color = Slate700,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "Profile",
                    fontSize = 11.sp,
                    color = RoyalBlue600,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick 4-Grid Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionItem(
                    title = "Scan QR",
                    icon = Icons.Default.QrCodeScanner,
                    backgroundColor = Color(0xFFEFF6FF),
                    iconColor = RoyalBlue600,
                    modifier = Modifier.weight(1f).testTag("action_scan_qr"),
                    onClick = onNavigateToScanner
                )
                QuickActionItem(
                    title = "Pay UPI",
                    icon = Icons.Default.Send,
                    backgroundColor = Color(0xFFF0FDF4),
                    iconColor = Emerald600,
                    modifier = Modifier.weight(1f).testTag("action_pay_contacts"),
                    onClick = { showSendMoneyDialog = true }
                )
                QuickActionItem(
                    title = "Check Bal",
                    icon = Icons.Default.AccountBalance,
                    backgroundColor = Color(0xFFFFFBEB),
                    iconColor = Amber500,
                    modifier = Modifier.weight(1f).testTag("action_check_balance"),
                    onClick = { onNavigateToBanks(1) } // Open directly to check balance tab
                )
                QuickActionItem(
                    title = "My QR",
                    icon = Icons.Default.QrCode,
                    backgroundColor = Color(0xFFFAF5FF),
                    iconColor = Color(0xFF9333EA),
                    modifier = Modifier.weight(1f).testTag("action_my_qr"),
                    onClick = onNavigateToMyQr
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Primary Bank & Balance Card
            if (primaryBank != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(20.dp))
                        .testTag("home_bank_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(
                                                    Color(primaryBank.cardGradientStart),
                                                    Color(primaryBank.cardGradientEnd)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = primaryBank.bankCode.take(2),
                                        color = SurfaceWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = primaryBank.bankName,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Navy900
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFDCFCE7))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "PRIMARY",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Emerald600
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${primaryBank.accountType} • ${primaryBank.accountNumberMasked}",
                                        fontSize = 12.sp,
                                        color = Slate500
                                    )
                                }
                            }

                            TextButton(
                                onClick = { onNavigateToBanks(0) },
                                modifier = Modifier.testTag("manage_banks_button")
                            ) {
                                Text(
                                    text = "All Banks (${bankAccounts.size})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalBlue600
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Account Balance Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Account Balance",
                                    fontSize = 11.sp,
                                    color = Slate500,
                                    fontWeight = FontWeight.Medium
                                )
                                val balance = revealedBalances[primaryBank.id]
                                if (balance != null) {
                                    Text(
                                        text = "₹${"%,.2f".format(balance)}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Navy900
                                    )
                                } else {
                                    Text(
                                        text = "••••••",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Slate400,
                                        letterSpacing = 2.sp
                                    )
                                }
                            }

                            Button(
                                onClick = { viewModel.initiateBalanceCheck(primaryBank, activity) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (revealedBalances[primaryBank.id] != null) Color(0xFFF1F5F9) else RoyalBlue600,
                                    contentColor = if (revealedBalances[primaryBank.id] != null) RoyalBlue600 else SurfaceWhite
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("quick_check_balance_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (revealedBalances[primaryBank.id] != null) "Refresh" else "Check Balance",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Daily Transaction Limit Security Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(16.dp))
                    .clickable { viewModel.openSetDailyLimitDialog() }
                    .testTag("home_daily_limit_card")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isDailyLimitEnabled) Color(0xFFEFF6FF) else Color(0xFFF1F5F9))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = if (isDailyLimitEnabled) RoyalBlue600 else Slate400,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Daily Spending Limit",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Navy900
                                )
                                Text(
                                    text = if (isDailyLimitEnabled) "Limit: ₹${"%,.0f".format(dailyTransactionLimit)}" else "Protection Paused",
                                    fontSize = 11.sp,
                                    color = if (isDailyLimitEnabled) Slate500 else Slate400
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isDailyLimitEnabled) Color(0xFFDCFCE7) else Color(0xFFF1F5F9))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isDailyLimitEnabled) "PROTECTED" else "OFF",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDailyLimitEnabled) Emerald600 else Slate500
                                )
                            }
                            IconButton(
                                onClick = { viewModel.openSetDailyLimitDialog() },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("home_edit_daily_limit_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Edit Limit",
                                    tint = RoyalBlue600,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    if (isDailyLimitEnabled) {
                        Spacer(modifier = Modifier.height(10.dp))

                        val progressColor = when {
                            dailyLimitProgress > 0.9f -> Crimson600
                            dailyLimitProgress > 0.7f -> Amber500
                            else -> Emerald600
                        }

                        LinearProgressIndicator(
                            progress = { dailyLimitProgress },
                            color = progressColor,
                            trackColor = Color(0xFFF1F5F9),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Spent Today: ₹${"%,.0f".format(todaySpentAmount)}",
                                fontSize = 11.sp,
                                color = Slate600
                            )
                            Text(
                                text = "₹${"%,.0f".format(remainingDailyLimit)} remaining",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (remainingDailyLimit < 1000) Crimson600 else Emerald600
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // People / Social Payment Contacts Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "People & Social Payments",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
                Text(
                    text = "Tap to chat",
                    fontSize = 12.sp,
                    color = Slate500
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(contacts, key = { it.id }) { contact ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(68.dp)
                            .clickable {
                                viewModel.openChat(contact)
                                onOpenContactChat(contact)
                            }
                            .testTag("contact_avatar_${contact.id}")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(contact.avatarColor)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = contact.name.take(1).uppercase(),
                                color = SurfaceWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }

                        Text(
                            text = contact.name.split(" ").first(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Navy900,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Guaranteed Cashback Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNavigateToRewards)
                    .shadow(3.dp, RoundedCornerShape(16.dp))
                    .testTag("cashback_promo_banner"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF1E3A8A), RoyalBlue600)
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ElectricBolt,
                                    contentDescription = null,
                                    tint = Color(0xFFFDE047),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "GUARANTEED CASHBACK",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFDE047),
                                    letterSpacing = 0.8.sp
                                )
                            }
                            Text(
                                text = "Win ₹10 - ₹85 on Every UPI Transfer",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SurfaceWhite,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            Text(
                                text = "Total won so far: ₹${"%.0f".format(totalCashbackWon ?: 0.0)} • Tap to view scratch cards",
                                fontSize = 11.sp,
                                color = Color(0xFFDBEAFE),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Open",
                            tint = SurfaceWhite
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Recent Transactions Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
                TextButton(
                    onClick = onNavigateToHistory,
                    modifier = Modifier.testTag("see_all_history_btn")
                ) {
                    Text(
                        text = "See All",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalBlue600
                    )
                }
            }

            // Quick preview of 3 transactions
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                transactions.take(3).forEach { tx ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onNavigateToHistory),
                        shape = RoundedCornerShape(14.dp),
                        color = SurfaceWhite,
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (tx.type == "RECEIVED") Color(0xFFDCFCE7) else Color(0xFFDBEAFE)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (tx.type == "RECEIVED") Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                        contentDescription = null,
                                        tint = if (tx.type == "RECEIVED") Emerald600 else RoyalBlue600,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = tx.contactName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Navy900
                                    )
                                    Text(
                                        text = "${tx.bankName} • ${tx.locationTag}",
                                        fontSize = 11.sp,
                                        color = Slate500
                                    )
                                }
                            }

                            Text(
                                text = (if (tx.type == "SENT") "- " else "+ ") + "₹${"%.2f".format(tx.amount)}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (tx.type == "RECEIVED") Emerald600 else Navy900
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Direct Send Money Modal Dialog
    if (showSendMoneyDialog) {
        var recipientVpa by remember { mutableStateOf("") }
        var recipientName by remember { mutableStateOf("") }
        var amountText by remember { mutableStateOf("") }
        var noteText by remember { mutableStateOf("") }
        var selectedBankId by remember { mutableStateOf(primaryBank?.id ?: "") }
        var simulateFailure by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showSendMoneyDialog = false },
            title = {
                Text(
                    text = "Send Money via UPI",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Navy900
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = recipientVpa,
                        onValueChange = {
                            recipientVpa = it
                            if (recipientName.isBlank()) recipientName = it.split("@").first()
                        },
                        label = { Text("Recipient UPI ID / Mobile") },
                        placeholder = { Text("e.g. priya@upi") },
                        modifier = Modifier.fillMaxWidth().testTag("send_recipient_vpa_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = recipientName,
                        onValueChange = { recipientName = it },
                        label = { Text("Recipient Name") },
                        modifier = Modifier.fillMaxWidth().testTag("send_recipient_name_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Amount (₹)") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.CurrencyRupee, contentDescription = null)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("send_amount_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        label = { Text("Note (optional)") },
                        modifier = Modifier.fillMaxWidth().testTag("send_note_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Optional failure simulation switch
                    Surface(
                        onClick = { simulateFailure = !simulateFailure },
                        color = if (simulateFailure) Color(0xFFFEF2F2) else Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (simulateFailure) Color(0xFFFECACA) else Slate200),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Simulate Bank Decline (Test Failure)",
                                fontSize = 12.sp,
                                color = if (simulateFailure) Crimson600 else Slate700,
                                fontWeight = if (simulateFailure) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = if (simulateFailure) "ON" else "OFF",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (simulateFailure) Crimson600 else Slate400
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = amountText.toDoubleOrNull()
                        if (amount != null && amount > 0 && recipientVpa.isNotBlank()) {
                            val finalNote = if (simulateFailure) "$noteText DECLINE" else noteText
                            viewModel.initiatePayment(
                                PendingPaymentAuth(
                                    recipientContactId = null,
                                    recipientName = recipientName.ifBlank { recipientVpa },
                                    recipientVpa = recipientVpa,
                                    amount = amount,
                                    bankAccountId = selectedBankId,
                                    note = finalNote
                                ),
                                activity = activity
                            )
                            showSendMoneyDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                    modifier = Modifier.testTag("submit_send_money_button")
                ) {
                    Text("Pay Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSendMoneyDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showProfileDialog) {
        UserProfileDialog(
            profile = userProfile,
            isLoggedIn = isUserLoggedIn,
            isSyncing = isCloudSyncing,
            syncStatusMessage = cloudSyncStatusMessage,
            onDismiss = { viewModel.setShowProfileDialog(false) },
            onSaveProfile = { name, vpa, phone, email, avatarType, avatarEmoji, avatarColor ->
                viewModel.updateUserProfile(name, vpa, phone, email)
                viewModel.updateAvatar(avatarType, avatarEmoji, avatarColor)
            },
            onSyncAll = {
                viewModel.syncAllWithCloud()
            },
            onLogout = {
                viewModel.logout()
            },
            onOpenLogin = {
                viewModel.openLoginDialog()
            }
        )
    }
}

@Composable
private fun QuickActionItem(
    title: String,
    icon: ImageVector,
    backgroundColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .shadow(1.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Navy900,
                textAlign = TextAlign.Center
            )
        }
    }
}
