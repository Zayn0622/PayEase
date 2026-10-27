package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Redeem
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.CashbackCelebrationDialog
import com.example.ui.components.DailyLimitExceededDialog
import com.example.ui.components.InsufficientBalanceDialog
import com.example.ui.components.PaymentResultAnimation
import com.example.ui.components.PaymentResultStatus
import com.example.ui.components.SecuritySettingsDialog
import com.example.ui.components.SetDailyLimitDialog
import com.example.ui.components.UpiPinBottomSheet
import com.example.ui.components.UserLoginDialog
import com.example.ui.screens.AppLockScreen
import com.example.ui.screens.BanksScreen
import com.example.ui.screens.ChatDetailScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyQrScreen
import com.example.ui.screens.QrScannerScreen
import com.example.ui.screens.RewardsScreen
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Navy900
import com.example.ui.theme.RoyalBlue50
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.SurfaceWhite

object Routes {
    const val HOME = "home"
    const val HISTORY = "history"
    const val BANKS = "banks"
    const val REWARDS = "rewards"
    const val SCANNER = "scanner"
    const val MY_QR = "my_qr"
    const val CHAT = "chat"
}

data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
)

@Composable
fun PayEaseApp(viewModel: PayEaseViewModel = viewModel()) {
    val navController = rememberNavController()

    val balanceCheckingAccount by viewModel.balanceCheckingAccount.collectAsState()
    val pinErrorMessage by viewModel.pinErrorMessage.collectAsState()
    val celebrationReward by viewModel.activeCelebrationReward.collectAsState()
    val activeContact by viewModel.activeContact.collectAsState()
    val unclaimedCount by viewModel.unclaimedRewardsCount.collectAsState()

    // Biometric Security States
    val isAppUnlocked by viewModel.isAppUnlocked.collectAsState()
    val isBiometricAppLockEnabled by viewModel.isBiometricAppLockEnabled.collectAsState()
    val showAppLockPinFallback by viewModel.showAppLockPinFallback.collectAsState()
    val appLockPinError by viewModel.appLockPinError.collectAsState()
    val showSecuritySettings by viewModel.showSecuritySettingsDialog.collectAsState()
    val isBiometricPaymentAuthEnabled by viewModel.isBiometricPaymentAuthEnabled.collectAsState()
    val isBiometricBalanceAuthEnabled by viewModel.isBiometricBalanceAuthEnabled.collectAsState()
    val pendingPayment by viewModel.pendingPaymentAuth.collectAsState()
    val paymentPinErrorMessage by viewModel.paymentPinErrorMessage.collectAsState()

    // Daily Transaction Limit States
    val isDailyLimitEnabled by viewModel.isDailyLimitEnabled.collectAsState()
    val dailyTransactionLimit by viewModel.dailyTransactionLimit.collectAsState()
    val todaySpentAmount by viewModel.todaySpentAmount.collectAsState()
    val dailyLimitExceeded by viewModel.dailyLimitExceeded.collectAsState()
    val showSetDailyLimitDialog by viewModel.showSetDailyLimitDialog.collectAsState()

    // Payment Result Animations & Insufficient Balance States
    val activePaymentResult by viewModel.activePaymentResult.collectAsState()
    val insufficientBalanceData by viewModel.insufficientBalanceData.collectAsState()
    val showLoginDialog by viewModel.showLoginDialog.collectAsState()
    val bankAccounts by viewModel.bankAccounts.collectAsState()

    val context = LocalContext.current
    val activity = context as? FragmentActivity

    var currentTab by remember { mutableStateOf(Routes.HOME) }
    var initialBankTab by remember { mutableIntStateOf(0) }

    val bottomNavItems = listOf(
        BottomNavItem(Routes.HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home, "bottom_nav_home"),
        BottomNavItem(Routes.HISTORY, "History", Icons.Filled.History, Icons.Outlined.History, "bottom_nav_history"),
        BottomNavItem(Routes.BANKS, "Banks", Icons.Filled.AccountBalance, Icons.Outlined.AccountBalance, "bottom_nav_banks"),
        BottomNavItem(Routes.REWARDS, "Rewards", Icons.Filled.Redeem, Icons.Outlined.Redeem, "bottom_nav_rewards")
    )

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Routes.HOME) {
                Scaffold(
                    bottomBar = {
                        PayEaseBottomBar(
                            currentRoute = currentTab,
                            items = bottomNavItems,
                            unclaimedRewards = unclaimedCount,
                            onItemClick = { route ->
                                currentTab = route
                                if (route != Routes.HOME) {
                                    navController.navigate(route) {
                                        popUpTo(Routes.HOME) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToScanner = { navController.navigate(Routes.SCANNER) },
                            onNavigateToMyQr = { navController.navigate(Routes.MY_QR) },
                            onNavigateToBanks = { tab ->
                                initialBankTab = tab
                                currentTab = Routes.BANKS
                                navController.navigate(Routes.BANKS)
                            },
                            onNavigateToHistory = {
                                currentTab = Routes.HISTORY
                                navController.navigate(Routes.HISTORY)
                            },
                            onNavigateToRewards = {
                                currentTab = Routes.REWARDS
                                navController.navigate(Routes.REWARDS)
                            },
                            onOpenContactChat = { contact ->
                                navController.navigate(Routes.CHAT)
                            }
                        )
                    }
                }
            }

            composable(Routes.HISTORY) {
                Scaffold(
                    bottomBar = {
                        PayEaseBottomBar(
                            currentRoute = Routes.HISTORY,
                            items = bottomNavItems,
                            unclaimedRewards = unclaimedCount,
                            onItemClick = { route ->
                                currentTab = route
                                navController.navigate(route) {
                                    popUpTo(Routes.HOME) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        HistoryScreen(
                            viewModel = viewModel,
                            onNavigateBack = {
                                currentTab = Routes.HOME
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }

            composable(Routes.BANKS) {
                Scaffold(
                    bottomBar = {
                        PayEaseBottomBar(
                            currentRoute = Routes.BANKS,
                            items = bottomNavItems,
                            unclaimedRewards = unclaimedCount,
                            onItemClick = { route ->
                                currentTab = route
                                navController.navigate(route) {
                                    popUpTo(Routes.HOME) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        BanksScreen(
                            viewModel = viewModel,
                            initialTab = initialBankTab,
                            onNavigateBack = {
                                currentTab = Routes.HOME
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }

            composable(Routes.REWARDS) {
                Scaffold(
                    bottomBar = {
                        PayEaseBottomBar(
                            currentRoute = Routes.REWARDS,
                            items = bottomNavItems,
                            unclaimedRewards = unclaimedCount,
                            onItemClick = { route ->
                                currentTab = route
                                navController.navigate(route) {
                                    popUpTo(Routes.HOME) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        RewardsScreen(
                            viewModel = viewModel,
                            onNavigateBack = {
                                currentTab = Routes.HOME
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }

            composable(Routes.SCANNER) {
                val banks by viewModel.bankAccounts.collectAsState()
                val primaryBank = banks.firstOrNull { it.isPrimary } ?: banks.firstOrNull()

                QrScannerScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMyQr = { navController.navigate(Routes.MY_QR) },
                    onPaymentQrDetected = { vpa, name, amt ->
                        viewModel.initiatePayment(
                            PendingPaymentAuth(
                                recipientContactId = null,
                                recipientName = name,
                                recipientVpa = vpa,
                                amount = amt ?: 150.0,
                                bankAccountId = primaryBank?.id ?: "",
                                note = "Instant UPI QR Payment"
                            ),
                            activity = activity
                        )
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.MY_QR) {
                MyQrScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToScanner = { navController.navigate(Routes.SCANNER) }
                )
            }

            composable(Routes.CHAT) {
                activeContact?.let { contact ->
                    ChatDetailScreen(
                        contact = contact,
                        viewModel = viewModel,
                        onNavigateBack = {
                            viewModel.closeChat()
                            navController.popBackStack()
                        }
                    )
                } ?: run {
                    navController.popBackStack()
                }
            }
        }

        // Sensitive UPI Payment PIN Dialog (Fallback if biometrics requires PIN or cancelled)
        pendingPayment?.let { payment ->
            UpiPinBottomSheet(
                title = "Authorize UPI Payment",
                subtitle = "Pay ₹${payment.amount} to ${payment.recipientName}",
                amountToAuthorize = payment.amount,
                errorMessage = paymentPinErrorMessage,
                onPinEntered = { pin ->
                    viewModel.verifyPaymentPinAndExecute(pin)
                },
                onDismiss = {
                    viewModel.cancelPaymentPinAuth()
                }
            )
        }

        // Global UPI PIN Bottom Sheet Dialog for Balance Check
        balanceCheckingAccount?.let { account ->
            UpiPinBottomSheet(
                title = "Check Account Balance",
                subtitle = "${account.bankName} ${account.accountNumberMasked}",
                amountToAuthorize = null,
                errorMessage = pinErrorMessage,
                onPinEntered = { pin ->
                    viewModel.verifyPinAndShowBalance(pin)
                },
                onDismiss = {
                    viewModel.cancelBalanceCheck()
                }
            )
        }

        // Security & Biometric Settings Sheet
        if (showSecuritySettings) {
            SecuritySettingsDialog(
                isAppLockEnabled = isBiometricAppLockEnabled,
                isPaymentAuthEnabled = isBiometricPaymentAuthEnabled,
                isBalanceAuthEnabled = isBiometricBalanceAuthEnabled,
                isDailyLimitEnabled = isDailyLimitEnabled,
                dailyTransactionLimit = dailyTransactionLimit,
                todaySpentAmount = todaySpentAmount,
                onToggleAppLock = { viewModel.toggleBiometricAppLock(it) },
                onTogglePaymentAuth = { viewModel.toggleBiometricPaymentAuth(it) },
                onToggleBalanceAuth = { viewModel.toggleBiometricBalanceAuth(it) },
                onToggleDailyLimit = { viewModel.toggleDailyLimit(it) },
                onConfigureDailyLimit = { viewModel.openSetDailyLimitDialog() },
                onLockAppNow = { viewModel.lockApp() },
                onDismiss = { viewModel.closeSecuritySettings() }
            )
        }

        // Daily Transaction Limit Configuration Dialog
        if (showSetDailyLimitDialog) {
            SetDailyLimitDialog(
                currentLimit = dailyTransactionLimit,
                isLimitEnabled = isDailyLimitEnabled,
                todaySpent = todaySpentAmount,
                onSaveLimit = { newLimit -> viewModel.updateDailyLimit(newLimit) },
                onToggleLimit = { viewModel.toggleDailyLimit(it) },
                onDismiss = { viewModel.closeSetDailyLimitDialog() }
            )
        }

        // Daily Limit Exceeded Error Alert Dialog
        dailyLimitExceeded?.let { info ->
            DailyLimitExceededDialog(
                info = info,
                onAdjustLimit = {
                    viewModel.dismissDailyLimitExceeded()
                    viewModel.openSetDailyLimitDialog()
                },
                onDismiss = {
                    viewModel.dismissDailyLimitExceeded()
                }
            )
        }

        // Unified Google Pay / PhonePe-style Payment Result Overlay (Processing -> Success / Failed)
        activePaymentResult?.let { result ->
            androidx.compose.ui.window.Dialog(
                onDismissRequest = {
                    if (result.status != PaymentResultStatus.PROCESSING) {
                        viewModel.dismissPaymentResult()
                    }
                },
                properties = androidx.compose.ui.window.DialogProperties(
                    dismissOnBackPress = result.status != PaymentResultStatus.PROCESSING,
                    dismissOnClickOutside = result.status != PaymentResultStatus.PROCESSING,
                    usePlatformDefaultWidth = false
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.78f))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .androidx.compose.ui.draw.clip(androidx.compose.foundation.shape.RoundedCornerShape(28.dp))
                            .testTag("payment_result_modal"),
                        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = SurfaceWhite),
                        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 14.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp, horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            PaymentResultAnimation(
                                status = result.status,
                                amount = result.payment.amount,
                                recipientName = result.payment.recipientName,
                                bankName = result.bankName,
                                errorMessage = result.errorMessage,
                                onTryAgain = {
                                    viewModel.retryActivePaymentResult(activity)
                                },
                                onDone = {
                                    val rew = result.reward
                                    viewModel.dismissPaymentResult()
                                    if (result.status == PaymentResultStatus.SUCCESS && rew != null) {
                                        viewModel.claimRewardFromSuccess(rew)
                                    }
                                }
                            )

                            // If SUCCESS and reward available, show quick scratch button
                            if (result.status == PaymentResultStatus.SUCCESS && result.reward != null) {
                                androidx.compose.material3.Button(
                                    onClick = {
                                        val rew = result.reward
                                        viewModel.dismissPaymentResult()
                                        viewModel.claimRewardFromSuccess(rew)
                                    },
                                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                        containerColor = androidx.compose.ui.graphics.Color(0xFFD97706)
                                    ),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .padding(top = 8.dp)
                                        .testTag("payment_claim_reward_button")
                                ) {
                                    androidx.compose.material3.Text(
                                        text = "🎁 Scratch & Reveal Cashback",
                                        fontWeight = FontWeight.Bold,
                                        color = androidx.compose.ui.graphics.Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Insufficient Balance Animated Alert Dialog
        insufficientBalanceData?.let { data ->
            InsufficientBalanceDialog(
                data = data,
                allBanks = bankAccounts,
                onSelectDifferentBankAndPay = { chosenBank ->
                    viewModel.switchBankAndPay(chosenBank, activity)
                },
                onAddFundsAndRetry = { amtToAdd ->
                    viewModel.depositAndRetryPayment(amtToAdd, activity)
                },
                onDismiss = {
                    viewModel.dismissInsufficientBalance()
                }
            )
        }

        // User Login Dialog
        if (showLoginDialog) {
            UserLoginDialog(
                onDismiss = { viewModel.dismissLoginDialog() },
                onLoginSuccess = { profile ->
                    viewModel.login(profile)
                }
            )
        }

        // Global Celebration Scratch Card & Popping Coin Dialog
        celebrationReward?.let { reward ->
            CashbackCelebrationDialog(
                reward = reward,
                onClaimReward = { amt ->
                    viewModel.claimCelebrationReward(amt)
                },
                onDismiss = {
                    viewModel.dismissCelebration()
                }
            )
        }

        // App Launch Biometric Lock Screen Overlay
        if (isBiometricAppLockEnabled && !isAppUnlocked) {
            AppLockScreen(
                onUnlockSuccess = { viewModel.unlockAppWithBiometrics() },
                onUsePinFallback = { viewModel.showPinFallback() }
            )

            if (showAppLockPinFallback) {
                UpiPinBottomSheet(
                    title = "Unlock PayEase",
                    subtitle = "Enter 4-Digit UPI PIN to unlock application",
                    amountToAuthorize = null,
                    errorMessage = appLockPinError,
                    onPinEntered = { pin ->
                        viewModel.unlockAppWithPin(pin)
                    },
                    onDismiss = {
                        viewModel.dismissPinFallback()
                    }
                )
            }
        }
    }
}

@Composable
private fun PayEaseBottomBar(
    currentRoute: String,
    items: List<BottomNavItem>,
    unclaimedRewards: Int,
    onItemClick: (String) -> Unit
) {
    NavigationBar(
        containerColor = SurfaceWhite,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemClick(item.route) },
                icon = {
                    if (item.route == Routes.REWARDS && unclaimedRewards > 0) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = Emerald600) {
                                    Text(unclaimedRewards.toString(), color = SurfaceWhite)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = item.title,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = RoyalBlue600,
                    selectedTextColor = RoyalBlue600,
                    unselectedIconColor = Slate400,
                    unselectedTextColor = Slate500,
                    indicatorColor = RoyalBlue50
                ),
                modifier = Modifier.testTag(item.tag)
            )
        }
    }
}
