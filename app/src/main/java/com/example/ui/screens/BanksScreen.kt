package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.data.local.entity.BankAccountEntity
import com.example.ui.PayEaseViewModel
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Navy900
import com.example.ui.theme.RoyalBlue50
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.SurfaceWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BanksScreen(
    viewModel: PayEaseViewModel,
    initialTab: Int = 0,
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    var showAddBankDialog by remember { mutableStateOf(false) }

    val bankAccounts by viewModel.bankAccounts.collectAsState()
    val revealedBalances by viewModel.revealedBalanceMap.collectAsState()

    val context = LocalContext.current
    val activity = context as? FragmentActivity

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (selectedTab == 0) "Linked Bank Accounts" else "Check Account Balance",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Navy900
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("banks_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Navy900
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.openSecuritySettings() },
                        modifier = Modifier.testTag("banks_security_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = "Security Settings",
                            tint = Emerald600
                        )
                    }

                    if (selectedTab == 0) {
                        IconButton(
                            onClick = { showAddBankDialog = true },
                            modifier = Modifier.testTag("add_bank_top_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Bank",
                                tint = RoyalBlue600
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = SurfaceWhite
                )
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showAddBankDialog = true },
                    containerColor = RoyalBlue600,
                    contentColor = SurfaceWhite,
                    modifier = Modifier.testTag("add_bank_fab")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Link New Bank",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        },
        containerColor = Slate50
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs: Linked Banks vs Check Balance
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceWhite,
                contentColor = RoyalBlue600,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = RoyalBlue600,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Linked Banks (${bankAccounts.size})",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier.testTag("tab_linked_banks")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Check Balance",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier.testTag("tab_check_balance")
                )
            }

            if (selectedTab == 0) {
                // Multi-Bank Linking Tab Content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        // NPCI Security Guarantee Banner
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEFF6FF))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Security",
                                tint = RoyalBlue600,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Accounts are linked via secure NPCI UPI protocol. Your bank credentials are never stored on PayEase servers.",
                                fontSize = 12.sp,
                                color = Slate700,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    items(bankAccounts, key = { it.id }) { bank ->
                        BankCardItem(
                            bank = bank,
                            onSetPrimary = { viewModel.setPrimaryBank(bank.id) },
                            onCheckBalance = { viewModel.initiateBalanceCheck(bank, activity) },
                            revealedBalance = revealedBalances[bank.id]
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            } else {
                // Check Balance Tab Content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Real-Time Balance Inquiries",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Navy900
                                )
                                Text(
                                    text = "Select any linked bank account below and enter your 4-digit UPI PIN to query the real-time balance directly from your bank's core system.",
                                    fontSize = 12.sp,
                                    color = Slate500,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }

                    items(bankAccounts, key = { it.id }) { bank ->
                        val balance = revealedBalances[bank.id]
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(2.dp, RoundedCornerShape(16.dp))
                                .testTag("check_balance_card_${bank.bankCode}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(
                                                        Color(bank.cardGradientStart),
                                                        Color(bank.cardGradientEnd)
                                                    )
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = bank.bankCode.take(2),
                                            color = SurfaceWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = bank.bankName,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Navy900
                                        )
                                        Text(
                                            text = "${bank.accountType} • ${bank.accountNumberMasked}",
                                            fontSize = 12.sp,
                                            color = Slate500
                                        )

                                        if (balance != null) {
                                            Text(
                                                text = "₹${"%,.2f".format(balance)}",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Emerald600,
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
                                        }
                                    }
                                }

                                Button(
                                    onClick = { viewModel.initiateBalanceCheck(bank, activity) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (balance != null) Color(0xFFF1F5F9) else RoyalBlue600,
                                        contentColor = if (balance != null) RoyalBlue600 else SurfaceWhite
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("verify_pin_btn_${bank.id}")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (balance != null) Icons.Default.Refresh else Icons.Default.Lock,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = if (balance != null) "Refresh" else "Check",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add New Bank Account Dialog
    if (showAddBankDialog) {
        val popularBanks = listOf(
            Pair("Kotak Mahindra Bank", "KOTAK"),
            Pair("Punjab National Bank", "PNB"),
            Pair("Bank of Baroda", "BOB"),
            Pair("Canara Bank", "CANARA"),
            Pair("Union Bank of India", "UBI"),
            Pair("IndusInd Bank", "INDUS")
        )

        var selectedBankToAdd by remember { mutableStateOf(popularBanks.first()) }
        var selectedAccType by remember { mutableStateOf("Savings Account") }

        AlertDialog(
            onDismissRequest = { showAddBankDialog = false },
            title = {
                Text(
                    text = "Link Another Bank Account",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Navy900
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Select your bank to discover accounts registered with your phone number:",
                        fontSize = 13.sp,
                        color = Slate500
                    )

                    popularBanks.forEach { (name, code) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (selectedBankToAdd.second == code) Color(0xFFEFF6FF) else Color.Transparent
                                )
                                .clickable { selectedBankToAdd = Pair(name, code) }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedBankToAdd.second == code,
                                onClick = { selectedBankToAdd = Pair(name, code) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = name,
                                fontSize = 14.sp,
                                fontWeight = if (selectedBankToAdd.second == code) FontWeight.Bold else FontWeight.Normal,
                                color = Navy900
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.linkNewBankAccount(
                            bankName = selectedBankToAdd.first,
                            bankCode = selectedBankToAdd.second,
                            type = selectedAccType
                        )
                        showAddBankDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600)
                ) {
                    Text("Link Account")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddBankDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun BankCardItem(
    bank: BankAccountEntity,
    onSetPrimary: () -> Unit,
    onCheckBalance: () -> Unit,
    revealedBalance: Double?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .testTag("bank_card_${bank.bankCode}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Column {
            // Gradient Header resembling a premium debit card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(bank.cardGradientStart),
                                Color(bank.cardGradientEnd)
                            )
                        )
                    )
                    .padding(18.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = bank.bankName,
                            color = SurfaceWhite,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (bank.isPrimary) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Emerald500)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "PRIMARY",
                                    color = SurfaceWhite,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.8.sp
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = bank.accountNumberMasked,
                                color = SurfaceWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "IFSC: ${bank.ifscCode} • ${bank.accountType}",
                                color = Color(0xFFE2E8F0),
                                fontSize = 11.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = "Card",
                            tint = SurfaceWhite.copy(alpha = 0.8f),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // Card Action Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (bank.isPrimary) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Emerald600,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Default for receiving UPI",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Emerald600
                        )
                    }
                } else {
                    TextButton(
                        onClick = onSetPrimary,
                        modifier = Modifier.testTag("set_primary_btn_${bank.id}")
                    ) {
                        Text(
                            text = "Set as Primary",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalBlue600
                        )
                    }
                }

                // Balance view or button
                if (revealedBalance != null) {
                    Text(
                        text = "₹${"%,.2f".format(revealedBalance)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Navy900
                    )
                } else {
                    OutlinedButton(
                        onClick = onCheckBalance,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("check_bal_${bank.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Check Balance",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
