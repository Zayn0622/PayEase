package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import coil.compose.AsyncImage
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ContactEntity
import com.example.ui.PayEaseViewModel
import com.example.ui.PendingPaymentAuth
import com.example.ui.theme.Crimson600
import com.example.ui.theme.Emerald50
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    contact: ContactEntity,
    viewModel: PayEaseViewModel,
    onNavigateBack: () -> Unit
) {
    val messages by viewModel.chatMessages.collectAsState()
    val banks by viewModel.bankAccounts.collectAsState()
    val primaryBank = banks.firstOrNull { it.isPrimary } ?: banks.firstOrNull()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Modals
    var showPayDialog by remember { mutableStateOf(false) }
    var showRequestDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? FragmentActivity

    // Visual media picker for zero-permission photo sharing
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.sendChatMessage(
                text = "Attached transaction receipt",
                attachmentUri = uri
            )
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(contact.avatarColor)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = contact.name.take(1).uppercase(),
                                color = SurfaceWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Column {
                            Text(
                                text = contact.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Navy900
                            )
                            Text(
                                text = contact.vpa,
                                fontSize = 12.sp,
                                color = Slate500
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("chat_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Navy900
                        )
                    }
                },
                actions = {
                    // Simulate Incoming Money Action to test real-time update
                    IconButton(
                        onClick = {
                            viewModel.simulateIncomingTransfer(
                                contactId = contact.id,
                                amount = 500.0,
                                note = "Payment settled via UPI"
                            )
                        },
                        modifier = Modifier.testTag("simulate_incoming_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Simulate Incoming",
                            tint = Emerald600
                        )
                    }

                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.testTag("chat_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = Slate700
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Simulate Incoming ₹1,000") },
                            onClick = {
                                viewModel.simulateIncomingTransfer(
                                    contactId = contact.id,
                                    amount = 1000.0,
                                    note = "Dinner split payment"
                                )
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Clear Conversation") },
                            onClick = { showMenu = false }
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
        ) {
            // Conversational Timeline
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    // Top Encryption Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Payments and messages are secured with 256-bit UPI encryption",
                            fontSize = 11.sp,
                            color = Slate500,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                items(messages, key = { it.id }) { message ->
                    ChatBubbleItem(
                        message = message,
                        onPayRequest = { reqMsg ->
                            viewModel.initiatePayment(
                                PendingPaymentAuth(
                                    recipientContactId = contact.id,
                                    recipientName = contact.name,
                                    recipientVpa = contact.vpa,
                                    amount = reqMsg.amount ?: 100.0,
                                    bankAccountId = primaryBank?.id ?: "",
                                    note = "Fulfilling request: ${reqMsg.text}"
                                ),
                                activity = activity
                            )
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Bottom Social Payment Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .border(1.dp, Slate200)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                // Quick Pay / Request actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showPayDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("chat_pay_action_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CurrencyRupee,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Pay",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Button(
                        onClick = { showRequestDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEFF6FF),
                            contentColor = RoyalBlue600
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("chat_request_action_button")
                    ) {
                        Text(
                            text = "Request",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Text & Attachment Input Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Attachment button (Photos/Videos receipt)
                    IconButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        modifier = Modifier.testTag("chat_attachment_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "Attach Receipt",
                            tint = Slate500
                        )
                    }

                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Message ${contact.name.split(" ").first()}...", fontSize = 14.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .testTag("chat_text_input"),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF1F5F9),
                            unfocusedContainerColor = Color(0xFFF1F5F9),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        maxLines = 3,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (inputText.isNotBlank()) {
                                    viewModel.sendChatMessage(inputText)
                                    inputText = ""
                                }
                            }
                        )
                    )

                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendChatMessage(inputText)
                                inputText = ""
                            }
                        },
                        enabled = inputText.isNotBlank(),
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (inputText.isNotBlank()) RoyalBlue600 else Color(0xFFE2E8F0))
                            .testTag("chat_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = if (inputText.isNotBlank()) SurfaceWhite else Slate400,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    // Pay Modal Dialog
    if (showPayDialog) {
        var payAmountText by remember { mutableStateOf("") }
        var payNoteText by remember { mutableStateOf("") }
        var selectedBankId by remember { mutableStateOf(primaryBank?.id ?: "") }

        AlertDialog(
            onDismissRequest = { showPayDialog = false },
            title = {
                Text(
                    text = "Pay ${contact.name}",
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
                        text = "To UPI ID: ${contact.vpa}",
                        fontSize = 13.sp,
                        color = Slate500
                    )

                    OutlinedTextField(
                        value = payAmountText,
                        onValueChange = { payAmountText = it },
                        label = { Text("Amount (₹)") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.CurrencyRupee, contentDescription = null)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("pay_amount_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = payNoteText,
                        onValueChange = { payNoteText = it },
                        label = { Text("Add a note (optional)") },
                        modifier = Modifier.fillMaxWidth().testTag("pay_note_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Bank Account Selector
                    Text(
                        text = "Pay from account:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate700
                    )

                    banks.forEach { b ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedBankId == b.id) Color(0xFFEFF6FF) else Color.Transparent)
                                .clickable { selectedBankId = b.id }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = if (selectedBankId == b.id) RoyalBlue600 else Slate400,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${b.bankName} ${b.accountNumberMasked}",
                                fontSize = 13.sp,
                                fontWeight = if (selectedBankId == b.id) FontWeight.Bold else FontWeight.Normal,
                                color = Navy900
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = payAmountText.toDoubleOrNull()
                        if (amount != null && amount > 0) {
                            viewModel.initiatePayment(
                                PendingPaymentAuth(
                                    recipientContactId = contact.id,
                                    recipientName = contact.name,
                                    recipientVpa = contact.vpa,
                                    amount = amount,
                                    bankAccountId = selectedBankId,
                                    note = payNoteText
                                ),
                                activity = activity
                            )
                            showPayDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                    modifier = Modifier.testTag("confirm_pay_submit_button")
                ) {
                    Text("Proceed to Pay")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPayDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Request Money Modal Dialog
    if (showRequestDialog) {
        var reqAmountText by remember { mutableStateOf("") }
        var reqNoteText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showRequestDialog = false },
            title = {
                Text(
                    text = "Request from ${contact.name}",
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
                    OutlinedTextField(
                        value = reqAmountText,
                        onValueChange = { reqAmountText = it },
                        label = { Text("Amount to Request (₹)") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.CurrencyRupee, contentDescription = null)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("req_amount_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = reqNoteText,
                        onValueChange = { reqNoteText = it },
                        label = { Text("What is this for?") },
                        modifier = Modifier.fillMaxWidth().testTag("req_note_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = reqAmountText.toDoubleOrNull()
                        if (amount != null && amount > 0) {
                            viewModel.requestMoneyFromContact(
                                contactId = contact.id,
                                amount = amount,
                                note = reqNoteText
                            )
                            showRequestDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                    modifier = Modifier.testTag("confirm_request_submit_button")
                ) {
                    Text("Send Request")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRequestDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ChatBubbleItem(
    message: ChatMessageEntity,
    onPayRequest: (ChatMessageEntity) -> Unit
) {
    val isMe = message.sender == "ME"
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(message.timestamp))

    when (message.messageType) {
        "PAYMENT_SENT", "PAYMENT_RECEIVED", "PAYMENT_REQUEST" -> {
            // Inline Transaction Card Bubble
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
            ) {
                Card(
                    modifier = Modifier
                        .width(280.dp)
                        .shadow(3.dp, RoundedCornerShape(18.dp))
                        .testTag("tx_bubble_${message.id}"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (message.messageType == "PAYMENT_RECEIVED") Color(0xFFF0FDF4) else SurfaceWhite
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (message.messageType) {
                                                "PAYMENT_RECEIVED" -> Color(0xFFDCFCE7)
                                                "PAYMENT_SENT" -> Color(0xFFDBEAFE)
                                                else -> Color(0xFFFEF3C7)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (message.messageType) {
                                            "PAYMENT_RECEIVED" -> Icons.Default.ArrowDownward
                                            "PAYMENT_SENT" -> Icons.Default.ArrowUpward
                                            else -> Icons.Default.Info
                                        },
                                        contentDescription = null,
                                        tint = when (message.messageType) {
                                            "PAYMENT_RECEIVED" -> Emerald600
                                            "PAYMENT_SENT" -> RoyalBlue600
                                            else -> Color(0xFFD97706)
                                        },
                                        modifier = Modifier.size(14.dp)
                                    )
                                }

                                Text(
                                    text = when (message.messageType) {
                                        "PAYMENT_RECEIVED" -> "Payment Received"
                                        "PAYMENT_SENT" -> "Payment Sent"
                                        else -> "Payment Requested"
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Navy900
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (message.status == "SUCCESS") Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = message.status,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (message.status == "SUCCESS") Emerald600 else Color(0xFFB45309)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "₹${"%.2f".format(message.amount ?: 0.0)}",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (message.messageType == "PAYMENT_RECEIVED") Emerald600 else Navy900
                        )

                        if (message.text.isNotBlank()) {
                            Text(
                                text = message.text,
                                fontSize = 12.sp,
                                color = Slate700,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        // Pay Action button if it's a pending request sent to me
                        if (message.messageType == "PAYMENT_REQUEST" && !isMe && message.status == "PENDING") {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { onPayRequest(message) },
                                colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(36.dp)
                            ) {
                                Text(
                                    text = "Pay ₹${"%.0f".format(message.amount ?: 0.0)} Now",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = formattedTime,
                            fontSize = 10.sp,
                            color = Slate400,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }

        "IMAGE_ATTACHMENT" -> {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
            ) {
                Column(
                    modifier = Modifier
                        .width(220.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isMe) RoyalBlue50 else SurfaceWhite)
                        .border(1.dp, Slate200, RoundedCornerShape(16.dp))
                        .padding(8.dp)
                ) {
                    if (message.attachmentUri != null) {
                        AsyncImage(
                            model = message.attachmentUri,
                            contentDescription = "Receipt Attachment",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = Slate400)
                        }
                    }

                    if (message.text.isNotBlank()) {
                        Text(
                            text = message.text,
                            fontSize = 12.sp,
                            color = Navy900,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    Text(
                        text = formattedTime,
                        fontSize = 10.sp,
                        color = Slate400,
                        modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                    )
                }
            }
        }

        else -> {
            // Standard Text Bubble
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
            ) {
                Column(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isMe) 16.dp else 4.dp,
                                bottomEnd = if (isMe) 4.dp else 16.dp
                            )
                        )
                        .background(if (isMe) RoyalBlue600 else SurfaceWhite)
                        .border(
                            1.dp,
                            if (isMe) Color.Transparent else Slate200,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = message.text,
                        fontSize = 14.sp,
                        color = if (isMe) SurfaceWhite else Navy900
                    )
                    Text(
                        text = formattedTime,
                        fontSize = 10.sp,
                        color = if (isMe) Color(0xFFDBEAFE) else Slate400,
                        modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
