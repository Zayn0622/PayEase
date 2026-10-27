package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BankAccountEntity
import com.example.data.local.entity.TransactionEntity
import com.example.ui.DateFilterType
import com.example.ui.PayEaseViewModel
import com.example.ui.TypeFilterType
import com.example.ui.theme.Amber500
import com.example.ui.theme.Crimson600
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Navy900
import com.example.ui.theme.RoyalBlue100
import com.example.ui.theme.RoyalBlue50
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.SurfaceWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: PayEaseViewModel,
    onNavigateBack: () -> Unit
) {
    val transactions by viewModel.filteredTransactions.collectAsState()
    val bankAccounts by viewModel.bankAccounts.collectAsState()

    val dateFilter by viewModel.selectedDateFilter.collectAsState()
    val typeFilter by viewModel.selectedTypeFilter.collectAsState()
    val bankFilterId by viewModel.selectedBankFilterId.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showFilterSheet by remember { mutableStateOf(false) }

    // Count of active non-default filters
    val activeFilterCount by remember(dateFilter, typeFilter, bankFilterId) {
        derivedStateOf {
            var count = 0
            if (dateFilter != DateFilterType.ALL) count++
            if (typeFilter != TypeFilterType.ALL) count++
            if (bankFilterId != null) count++
            count
        }
    }

    // Totals for filtered data
    val totalSent = transactions.filter { it.type == "SENT" && it.status == "SUCCESS" }.sumOf { it.amount }
    val totalReceived = transactions.filter { it.type == "RECEIVED" && it.status == "SUCCESS" }.sumOf { it.amount }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Transaction History",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Navy900
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("history_back_button")
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
                        onClick = { showFilterSheet = true },
                        modifier = Modifier.testTag("open_filters_sheet_button")
                    ) {
                        if (activeFilterCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(containerColor = RoyalBlue600) {
                                        Text(text = activeFilterCount.toString(), color = SurfaceWhite)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Filter Options",
                                    tint = RoyalBlue600
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Filter Options",
                                tint = Slate700
                            )
                        }
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
        ) {
            // Search Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = SurfaceWhite,
                shadowElevation = 1.dp
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search by name, UPI ID, note...", fontSize = 14.sp, color = Slate400) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Slate500)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = Slate500)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("history_search_input"),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SurfaceWhite,
                        unfocusedContainerColor = SurfaceWhite,
                        focusedIndicatorColor = RoyalBlue600,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }

            // Quick Interactive Filter Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Open all filters chip
                Surface(
                    onClick = { showFilterSheet = true },
                    shape = RoundedCornerShape(20.dp),
                    color = if (activeFilterCount > 0) RoyalBlue50 else SurfaceWhite,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (activeFilterCount > 0) RoyalBlue600 else Slate200
                    ),
                    modifier = Modifier.testTag("filter_all_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            tint = if (activeFilterCount > 0) RoyalBlue600 else Slate700,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (activeFilterCount > 0) "$activeFilterCount Active" else "All Filters",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (activeFilterCount > 0) RoyalBlue600 else Slate700
                        )
                    }
                }

                // Date Chip
                FilterChip(
                    selected = dateFilter != DateFilterType.ALL,
                    onClick = { showFilterSheet = true },
                    label = {
                        Text(
                            text = "Date: ${dateFilter.label}",
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(14.dp))
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RoyalBlue50,
                        selectedLabelColor = RoyalBlue600
                    ),
                    modifier = Modifier.testTag("date_filter_chip")
                )

                // Type Chip
                FilterChip(
                    selected = typeFilter != TypeFilterType.ALL,
                    onClick = { showFilterSheet = true },
                    label = {
                        Text(
                            text = "Type: ${typeFilter.label}",
                            fontSize = 12.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RoyalBlue50,
                        selectedLabelColor = RoyalBlue600
                    ),
                    modifier = Modifier.testTag("type_filter_chip")
                )

                // Bank Account Chip
                val currentBankName = bankAccounts.firstOrNull { it.id == bankFilterId }?.bankName ?: "All Banks"
                FilterChip(
                    selected = bankFilterId != null,
                    onClick = { showFilterSheet = true },
                    label = {
                        Text(
                            text = "Bank: $currentBankName",
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(14.dp))
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RoyalBlue50,
                        selectedLabelColor = RoyalBlue600
                    ),
                    modifier = Modifier.testTag("bank_filter_chip")
                )

                // Reset option if filters are active
                if (activeFilterCount > 0 || searchQuery.isNotEmpty()) {
                    TextButton(
                        onClick = { viewModel.resetAllFilters() },
                        modifier = Modifier.testTag("reset_quick_chip")
                    ) {
                        Text(
                            text = "Clear All",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Crimson600
                        )
                    }
                }
            }

            // Summary Info Bar for current filtered set
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${transactions.size} transactions found",
                    fontSize = 12.sp,
                    color = Slate500,
                    fontWeight = FontWeight.Medium
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Out: ₹${"%.0f".format(totalSent)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Navy900
                    )
                    Text(
                        text = "In: ₹${"%.0f".format(totalReceived)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Emerald600
                    )
                }
            }

            // Transactions List or Empty State
            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HourglassEmpty,
                                contentDescription = null,
                                tint = Slate500,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "No Transactions Found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )

                        Text(
                            text = "No records matched your selected date, type, or bank filters.",
                            fontSize = 13.sp,
                            color = Slate500,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(top = 6.dp)
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = { viewModel.resetAllFilters() },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("empty_state_reset_btn")
                        ) {
                            Text("Reset All Filters")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(transactions, key = { it.id }) { tx ->
                        TransactionDetailCard(tx)
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    // Advanced Filtering Bottom Sheet
    if (showFilterSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            containerColor = SurfaceWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            modifier = Modifier.testTag("advanced_filter_bottom_sheet")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 36.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter Transactions",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Navy900
                    )

                    TextButton(
                        onClick = {
                            viewModel.resetAllFilters()
                        },
                        modifier = Modifier.testTag("sheet_reset_button")
                    ) {
                        Text(
                            text = "Reset All",
                            fontWeight = FontWeight.Bold,
                            color = Crimson600
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Slate200)

                // 1. DATE RANGE SECTION
                Text(
                    text = "DATE RANGE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate500,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DateFilterType.values().forEach { d ->
                        val isSelected = dateFilter == d
                        Surface(
                            onClick = { viewModel.setDateFilter(d) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) RoyalBlue600 else Color(0xFFF1F5F9),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("filter_date_${d.name}")
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = d.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) SurfaceWhite else Slate700,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 2. TRANSACTION TYPE SECTION
                Text(
                    text = "TRANSACTION TYPE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate500,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TypeFilterType.values().forEach { t ->
                        val isSelected = typeFilter == t
                        Surface(
                            onClick = { viewModel.setTypeFilter(t) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) RoyalBlue600 else Color(0xFFF1F5F9),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("filter_type_${t.name}")
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = t.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) SurfaceWhite else Slate700,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 3. LINKED BANK ACCOUNT SECTION
                Text(
                    text = "SPECIFIC LINKED BANK ACCOUNT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate500,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                // All Banks option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (bankFilterId == null) Color(0xFFEFF6FF) else Color(0xFFF8FAFC))
                        .clickable { viewModel.setBankFilter(null) }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .testTag("bank_filter_all"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = bankFilterId == null,
                        onClick = { viewModel.setBankFilter(null) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "All Linked Bank Accounts",
                        fontSize = 14.sp,
                        fontWeight = if (bankFilterId == null) FontWeight.Bold else FontWeight.Normal,
                        color = Navy900
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                bankAccounts.forEach { bank ->
                    val isSelected = bankFilterId == bank.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC))
                            .clickable { viewModel.setBankFilter(bank.id) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("bank_filter_${bank.bankCode}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { viewModel.setBankFilter(bank.id) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = bank.bankName,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = Navy900
                            )
                            Text(
                                text = "${bank.accountType} • ${bank.accountNumberMasked}",
                                fontSize = 11.sp,
                                color = Slate500
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Apply Button
                Button(
                    onClick = { showFilterSheet = false },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("apply_filters_button")
                ) {
                    Text(
                        text = "Apply Filters (${transactions.size} Results)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SurfaceWhite
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionDetailCard(tx: TransactionEntity) {
    val isSent = tx.type == "SENT"
    val isReceived = tx.type == "RECEIVED"
    val isPending = tx.status == "PENDING"

    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(tx.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(16.dp))
            .testTag("transaction_item_${tx.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Type Icon
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isReceived -> Color(0xFFDCFCE7)
                                    isSent -> Color(0xFFDBEAFE)
                                    else -> Color(0xFFFEF3C7)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                isReceived -> Icons.Default.ArrowDownward
                                isSent -> Icons.Default.ArrowUpward
                                else -> Icons.Default.HourglassEmpty
                            },
                            contentDescription = null,
                            tint = when {
                                isReceived -> Emerald600
                                isSent -> RoyalBlue600
                                else -> Color(0xFFD97706)
                            },
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = tx.contactName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Navy900,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = tx.contactVpa,
                            fontSize = 11.sp,
                            color = Slate500
                        )
                    }
                }

                // Amount
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = (if (isSent) "- " else if (isReceived) "+ " else "") + "₹${"%,.2f".format(tx.amount)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = when {
                            isReceived -> Emerald600
                            isSent -> Navy900
                            else -> Color(0xFFB45309)
                        }
                    )

                    // Status pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (tx.status == "SUCCESS") Color(0xFFF0FDF4) else Color(0xFFFEF3C7)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = tx.status,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (tx.status == "SUCCESS") Emerald600 else Color(0xFFB45309)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            // Metadata row: Bank name, Location, Note, Cashback
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = Slate400,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = tx.bankName,
                        fontSize = 11.sp,
                        color = Slate500
                    )
                    Text(text = "•", fontSize = 11.sp, color = Slate400)
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Slate400,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = tx.locationTag,
                        fontSize = 11.sp,
                        color = Slate500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = formattedDate,
                    fontSize = 10.sp,
                    color = Slate400
                )
            }

            if (tx.cashbackWon > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFEF3C7))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Redeem,
                        contentDescription = null,
                        tint = Amber500,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Won ₹${"%.0f".format(tx.cashbackWon)} guaranteed cashback",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF92400E)
                    )
                }
            }

            if (tx.note.isNotBlank()) {
                Text(
                    text = "“${tx.note}”",
                    fontSize = 12.sp,
                    color = Slate700,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
