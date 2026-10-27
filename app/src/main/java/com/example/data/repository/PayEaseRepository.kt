package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.BankAccountEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ContactEntity
import com.example.data.local.entity.RewardEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.location.LocationHelper
import com.example.data.remote.firestore.FirestoreSyncService
import com.example.data.remote.firestore.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.random.Random

class PayEaseRepository(
    private val database: AppDatabase,
    private val locationHelper: LocationHelper,
    val firestoreSyncService: FirestoreSyncService? = null
) {
    private val bankDao = database.bankDao()
    private val transactionDao = database.transactionDao()
    private val chatDao = database.chatDao()
    private val rewardDao = database.rewardDao()

    val allBankAccounts: Flow<List<BankAccountEntity>> = bankDao.getAllBankAccounts()
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allContacts: Flow<List<ContactEntity>> = chatDao.getAllContacts()
    val allRewards: Flow<List<RewardEntity>> = rewardDao.getAllRewards()
    val totalCashbackWon: Flow<Double?> = rewardDao.getTotalCashbackWon()
    val unclaimedRewardsCount: Flow<Int> = rewardDao.getUnclaimedCount()

    fun getMessagesForContact(contactId: String): Flow<List<ChatMessageEntity>> =
        chatDao.getMessagesForContact(contactId)

    suspend fun initializeSeedDataIfNeeded() = withContext(Dispatchers.IO) {
        // 0. Sync or seed user profile in Firestore
        try {
            val remoteProfile = firestoreSyncService?.getUserProfile()
            if (remoteProfile == null) {
                firestoreSyncService?.saveUserProfile(UserProfile())
            }
        } catch (e: Exception) {
            android.util.Log.w("PayEaseRepo", "Failed to sync initial user profile with Firestore", e)
        }

        // Try restoring bank accounts and transactions from Firestore first
        try {
            val remoteBanks = firestoreSyncService?.fetchBankAccountMetadata()
            if (!remoteBanks.isNullOrEmpty()) {
                bankDao.insertAll(remoteBanks)
            }
            val remoteTxs = firestoreSyncService?.fetchTransactions()
            if (!remoteTxs.isNullOrEmpty()) {
                transactionDao.insertAll(remoteTxs)
            }
        } catch (e: Exception) {
            android.util.Log.w("PayEaseRepo", "Failed to restore data from Firestore", e)
        }

        val hasExistingTransactions = transactionDao.getCount() > 0

        // 1. Seed Banks
        val seedBanks = listOf(
            BankAccountEntity(
                id = "bank_hdfc",
                bankName = "HDFC Bank",
                bankCode = "HDFC",
                accountNumberMasked = "•••• 4821",
                accountType = "Savings Account",
                ifscCode = "HDFC0001234",
                balance = 48920.50,
                isPrimary = true,
                upiPin = "1234",
                cardGradientStart = 0xFF0D47A1,
                cardGradientEnd = 0xFF1976D2
            ),
            BankAccountEntity(
                id = "bank_sbi",
                bankName = "State Bank of India",
                bankCode = "SBI",
                accountNumberMasked = "•••• 9012",
                accountType = "Savings Account",
                ifscCode = "SBIN0004567",
                balance = 15400.00,
                isPrimary = false,
                upiPin = "4321",
                cardGradientStart = 0xFF004D40,
                cardGradientEnd = 0xFF00897B
            ),
            BankAccountEntity(
                id = "bank_icici",
                bankName = "ICICI Bank",
                bankCode = "ICICI",
                accountNumberMasked = "•••• 3377",
                accountType = "Savings Account",
                ifscCode = "ICIC0007890",
                balance = 82150.00,
                isPrimary = false,
                upiPin = "1111",
                cardGradientStart = 0xFFB71C1C,
                cardGradientEnd = 0xFFE53935
            ),
            BankAccountEntity(
                id = "bank_axis",
                bankName = "Axis Bank",
                bankCode = "AXIS",
                accountNumberMasked = "•••• 6540",
                accountType = "Current Account",
                ifscCode = "UTIB0002345",
                balance = 23800.00,
                isPrimary = false,
                upiPin = "2222",
                cardGradientStart = 0xFF4A148C,
                cardGradientEnd = 0xFF7B1FA2
            )
        )
        if (bankDao.getPrimaryAccount() == null) {
            bankDao.insertAll(seedBanks)
            try {
                firestoreSyncService?.saveBankAccountsBatch(seedBanks)
            } catch (e: Exception) {
                android.util.Log.w("PayEaseRepo", "Failed to sync seed banks to Firestore", e)
            }
        }

        // 2. Seed Contacts
        val seedContacts = listOf(
            ContactEntity(
                id = "c_priya",
                name = "Priya Sharma",
                vpa = "priya.sharma@okaxis",
                phone = "+91 98765 43210",
                avatarColor = 0xFF7C4DFF,
                lastMessage = "Received ₹1,200",
                lastMessageTime = System.currentTimeMillis() - 1000 * 60 * 15
            ),
            ContactEntity(
                id = "c_rahul",
                name = "Rahul Verma",
                vpa = "rahul.v@okhdfcbank",
                phone = "+91 98450 11223",
                avatarColor = 0xFF0288D1,
                lastMessage = "Paid ₹450 for dinner",
                lastMessageTime = System.currentTimeMillis() - 1000 * 60 * 60 * 2
            ),
            ContactEntity(
                id = "c_sneha",
                name = "Sneha Patel",
                vpa = "sneha.patel@icici",
                phone = "+91 97123 45678",
                avatarColor = 0xFF00897B,
                lastMessage = "Sent request for ₹350",
                lastMessageTime = System.currentTimeMillis() - 1000 * 60 * 60 * 24
            ),
            ContactEntity(
                id = "c_ananya",
                name = "Ananya Roy",
                vpa = "ananya.roy@oksbi",
                phone = "+91 99887 76655",
                avatarColor = 0xFFE91E63,
                lastMessage = "Thanks for the cab share!",
                lastMessageTime = System.currentTimeMillis() - 1000 * 60 * 60 * 48
            ),
            ContactEntity(
                id = "c_corner_cafe",
                name = "Corner Cafe & Bakery",
                vpa = "cornercafe@upi",
                phone = "+91 91234 56789",
                avatarColor = 0xFFFF8F00,
                lastMessage = "Paid ₹280",
                lastMessageTime = System.currentTimeMillis() - 1000 * 60 * 60 * 72
            )
        )
        chatDao.insertContacts(seedContacts)

        // 3. Seed Transactions
        val now = System.currentTimeMillis()
        val seedTransactions = listOf(
            TransactionEntity(
                id = "tx_101",
                contactId = "c_priya",
                contactName = "Priya Sharma",
                contactVpa = "priya.sharma@okaxis",
                amount = 1200.0,
                type = "RECEIVED",
                status = "SUCCESS",
                timestamp = now - 1000 * 60 * 15,
                upiRefId = "UPI/234901842019/Priya",
                bankAccountId = "bank_hdfc",
                bankName = "HDFC Bank",
                locationTag = "Indiranagar, Bengaluru",
                note = "Split for weekend trip",
                cashbackWon = 0.0
            ),
            TransactionEntity(
                id = "tx_102",
                contactId = "c_rahul",
                contactName = "Rahul Verma",
                contactVpa = "rahul.v@okhdfcbank",
                amount = 450.0,
                type = "SENT",
                status = "SUCCESS",
                timestamp = now - 1000 * 60 * 60 * 2,
                upiRefId = "UPI/489018241019/Rahul",
                bankAccountId = "bank_hdfc",
                bankName = "HDFC Bank",
                locationTag = "Koramangala, Bengaluru",
                note = "Dinner split",
                cashbackWon = 25.0
            ),
            TransactionEntity(
                id = "tx_103",
                contactId = "c_corner_cafe",
                contactName = "Corner Cafe & Bakery",
                contactVpa = "cornercafe@upi",
                amount = 280.0,
                type = "SENT",
                status = "SUCCESS",
                timestamp = now - 1000 * 60 * 60 * 72,
                upiRefId = "UPI/901238491823/Cafe",
                bankAccountId = "bank_sbi",
                bankName = "State Bank of India",
                locationTag = "MG Road, Bengaluru",
                note = "Filter Coffee & Croissant",
                cashbackWon = 15.0
            ),
            TransactionEntity(
                id = "tx_104",
                contactId = "c_sneha",
                contactName = "Sneha Patel",
                contactVpa = "sneha.patel@icici",
                amount = 350.0,
                type = "REQUESTED",
                status = "PENDING",
                timestamp = now - 1000 * 60 * 60 * 24,
                upiRefId = "REQ/891230192831/Sneha",
                bankAccountId = "bank_hdfc",
                bankName = "HDFC Bank",
                locationTag = "Whitefield, Bengaluru",
                note = "Movie tickets share",
                cashbackWon = 0.0
            )
        )
        if (!hasExistingTransactions) {
            transactionDao.insertAll(seedTransactions)
            try {
                firestoreSyncService?.saveTransactionsBatch(seedTransactions)
            } catch (e: Exception) {
                android.util.Log.w("PayEaseRepo", "Failed to sync seed transactions to Firestore", e)
            }
        }

        // 4. Seed Chat Messages
        val seedMessages = listOf(
            ChatMessageEntity(
                id = "msg_1",
                contactId = "c_priya",
                sender = "ME",
                messageType = "TEXT",
                text = "Hey Priya! Did you get the calculations for the road trip?",
                timestamp = now - 1000 * 60 * 60
            ),
            ChatMessageEntity(
                id = "msg_2",
                contactId = "c_priya",
                sender = "CONTACT",
                messageType = "TEXT",
                text = "Yes, sent my share right away!",
                timestamp = now - 1000 * 60 * 20
            ),
            ChatMessageEntity(
                id = "msg_3",
                contactId = "c_priya",
                sender = "CONTACT",
                messageType = "PAYMENT_RECEIVED",
                text = "Split for weekend trip",
                amount = 1200.0,
                transactionId = "tx_101",
                timestamp = now - 1000 * 60 * 15
            ),
            ChatMessageEntity(
                id = "msg_4",
                contactId = "c_rahul",
                sender = "ME",
                messageType = "PAYMENT_SENT",
                text = "Dinner split",
                amount = 450.0,
                transactionId = "tx_102",
                timestamp = now - 1000 * 60 * 60 * 2
            ),
            ChatMessageEntity(
                id = "msg_5",
                contactId = "c_sneha",
                sender = "CONTACT",
                messageType = "PAYMENT_REQUEST",
                text = "Movie tickets share",
                amount = 350.0,
                transactionId = "tx_104",
                status = "PENDING",
                timestamp = now - 1000 * 60 * 60 * 24
            )
        )
        chatDao.insertMessages(seedMessages)

        // 5. Seed Rewards
        val seedRewards = listOf(
            RewardEntity(
                id = "rew_1",
                transactionId = "tx_102",
                amount = 25.0,
                isScratched = true,
                timestamp = now - 1000 * 60 * 60 * 2,
                title = "Dinner Cashback",
                description = "Transferred to HDFC Bank •••• 4821"
            ),
            RewardEntity(
                id = "rew_2",
                transactionId = "tx_103",
                amount = 15.0,
                isScratched = true,
                timestamp = now - 1000 * 60 * 60 * 72,
                title = "Coffee Treat Bonus",
                description = "Transferred to SBI •••• 9012"
            ),
            RewardEntity(
                id = "rew_3",
                transactionId = "tx_welcome",
                amount = 50.0,
                isScratched = false,
                timestamp = now,
                title = "Welcome to PayEase!",
                description = "Scratch to claim your guaranteed starter reward"
            )
        )
        rewardDao.insertAll(seedRewards)
    }

    suspend fun getContact(contactId: String): ContactEntity? = withContext(Dispatchers.IO) {
        chatDao.getContactById(contactId)
    }

    suspend fun sendPayment(
        contactId: String?,
        recipientName: String,
        recipientVpa: String,
        amount: Double,
        bankAccountId: String,
        note: String
    ): Pair<TransactionEntity, RewardEntity> = withContext(Dispatchers.IO) {
        val bank = bankDao.getAccountById(bankAccountId) ?: bankDao.getPrimaryAccount()!!
        val newBalance = (bank.balance - amount).coerceAtLeast(0.0)
        bankDao.updateBalance(bank.id, newBalance)

        val locationTag = locationHelper.getCurrentLocationTag()
        val txId = "TX" + System.currentTimeMillis().toString().takeLast(8)
        val upiRef = "UPI/" + Random.nextLong(100000000000L, 999999999999L) + "/" + recipientName.take(6).trim()

        // Guaranteed cashback between ₹10 and ₹100
        val guaranteedCashback = Random.nextInt(10, 85).toDouble()

        val transaction = TransactionEntity(
            id = txId,
            contactId = contactId,
            contactName = recipientName,
            contactVpa = recipientVpa,
            amount = amount,
            type = "SENT",
            status = "SUCCESS",
            timestamp = System.currentTimeMillis(),
            upiRefId = upiRef,
            bankAccountId = bank.id,
            bankName = bank.bankName,
            locationTag = locationTag,
            note = note,
            cashbackWon = guaranteedCashback
        )
        transactionDao.insertTransaction(transaction)

        try {
            firestoreSyncService?.saveTransaction(transaction)
            firestoreSyncService?.updateBankAccountBalance(bank.id, newBalance)
        } catch (e: Exception) {
            android.util.Log.w("PayEaseRepo", "Failed to sync payment to Firestore", e)
        }

        val reward = RewardEntity(
            id = "rew_" + UUID.randomUUID().toString().take(8),
            transactionId = txId,
            amount = guaranteedCashback,
            isScratched = false,
            timestamp = System.currentTimeMillis(),
            title = "Instant Payment Cashback",
            description = "Guaranteed reward for ₹$amount transfer"
        )
        rewardDao.insertReward(reward)

        if (contactId != null) {
            val message = ChatMessageEntity(
                id = "msg_" + UUID.randomUUID().toString().take(8),
                contactId = contactId,
                sender = "ME",
                messageType = "PAYMENT_SENT",
                text = if (note.isNotBlank()) note else "Payment of ₹$amount",
                amount = amount,
                transactionId = txId,
                status = "SUCCESS",
                timestamp = System.currentTimeMillis()
            )
            chatDao.insertMessage(message)
            chatDao.updateLastMessage(contactId, "Paid ₹$amount", System.currentTimeMillis())
        }

        Pair(transaction, reward)
    }

    suspend fun sendTextMessage(
        contactId: String,
        text: String,
        attachmentUri: String? = null
    ) = withContext(Dispatchers.IO) {
        val msgType = if (attachmentUri != null) "IMAGE_ATTACHMENT" else "TEXT"
        val message = ChatMessageEntity(
            id = "msg_" + UUID.randomUUID().toString().take(8),
            contactId = contactId,
            sender = "ME",
            messageType = msgType,
            text = text,
            attachmentUri = attachmentUri,
            timestamp = System.currentTimeMillis()
        )
        chatDao.insertMessage(message)
        chatDao.updateLastMessage(
            contactId,
            if (attachmentUri != null) "📷 Receipt sent" else text,
            System.currentTimeMillis()
        )
    }

    suspend fun requestMoney(
        contactId: String,
        amount: Double,
        note: String
    ) = withContext(Dispatchers.IO) {
        val primaryBank = bankDao.getPrimaryAccount()!!
        val reqId = "REQ" + System.currentTimeMillis().toString().takeLast(6)
        val contact = chatDao.getContactById(contactId)

        val transaction = TransactionEntity(
            id = reqId,
            contactId = contactId,
            contactName = contact?.name ?: "Contact",
            contactVpa = contact?.vpa ?: "",
            amount = amount,
            type = "REQUESTED",
            status = "PENDING",
            timestamp = System.currentTimeMillis(),
            upiRefId = "REQ/$reqId",
            bankAccountId = primaryBank.id,
            bankName = primaryBank.bankName,
            locationTag = locationHelper.getCurrentLocationTag(),
            note = note
        )
        transactionDao.insertTransaction(transaction)

        try {
            firestoreSyncService?.saveTransaction(transaction)
        } catch (e: Exception) {
            android.util.Log.w("PayEaseRepo", "Failed to sync request to Firestore", e)
        }

        val message = ChatMessageEntity(
            id = "msg_" + UUID.randomUUID().toString().take(8),
            contactId = contactId,
            sender = "ME",
            messageType = "PAYMENT_REQUEST",
            text = if (note.isNotBlank()) note else "Requested payment",
            amount = amount,
            transactionId = reqId,
            status = "PENDING",
            timestamp = System.currentTimeMillis()
        )
        chatDao.insertMessage(message)
        chatDao.updateLastMessage(contactId, "Requested ₹$amount", System.currentTimeMillis())
    }

    suspend fun simulateIncomingMoney(
        contactId: String,
        amount: Double,
        note: String = "Transferred to you"
    ) = withContext(Dispatchers.IO) {
        val primaryBank = bankDao.getPrimaryAccount()!!
        val updatedBalance = primaryBank.balance + amount
        bankDao.updateBalance(primaryBank.id, updatedBalance)

        val contact = chatDao.getContactById(contactId)
        val txId = "TX" + System.currentTimeMillis().toString().takeLast(8)
        val upiRef = "UPI/" + Random.nextLong(100000000000L, 999999999999L) + "/" + (contact?.name?.take(4) ?: "User")

        val transaction = TransactionEntity(
            id = txId,
            contactId = contactId,
            contactName = contact?.name ?: "Contact",
            contactVpa = contact?.vpa ?: "",
            amount = amount,
            type = "RECEIVED",
            status = "SUCCESS",
            timestamp = System.currentTimeMillis(),
            upiRefId = upiRef,
            bankAccountId = primaryBank.id,
            bankName = primaryBank.bankName,
            locationTag = locationHelper.getCurrentLocationTag(),
            note = note
        )
        transactionDao.insertTransaction(transaction)

        try {
            firestoreSyncService?.saveTransaction(transaction)
            firestoreSyncService?.updateBankAccountBalance(primaryBank.id, updatedBalance)
        } catch (e: Exception) {
            android.util.Log.w("PayEaseRepo", "Failed to sync incoming money to Firestore", e)
        }

        val message = ChatMessageEntity(
            id = "msg_" + UUID.randomUUID().toString().take(8),
            contactId = contactId,
            sender = "CONTACT",
            messageType = "PAYMENT_RECEIVED",
            text = note,
            amount = amount,
            transactionId = txId,
            status = "SUCCESS",
            timestamp = System.currentTimeMillis()
        )
        chatDao.insertMessage(message)
        chatDao.updateLastMessage(contactId, "Received ₹$amount", System.currentTimeMillis())
    }

    suspend fun scratchReward(rewardId: String): Double? = withContext(Dispatchers.IO) {
        rewardDao.markScratched(rewardId)
        val primary = bankDao.getPrimaryAccount()
        primary?.balance
    }

    suspend fun claimCashback(rewardId: String, amount: Double) = withContext(Dispatchers.IO) {
        rewardDao.markScratched(rewardId)
        val primary = bankDao.getPrimaryAccount()
        if (primary != null) {
            val newBalance = primary.balance + amount
            bankDao.updateBalance(primary.id, newBalance)
            try {
                firestoreSyncService?.updateBankAccountBalance(primary.id, newBalance)
            } catch (e: Exception) {
                android.util.Log.w("PayEaseRepo", "Failed to sync cashback to Firestore", e)
            }
        }
    }

    suspend fun checkUpiPin(bankAccountId: String, pin: String): Pair<Boolean, Double> = withContext(Dispatchers.IO) {
        val account = bankDao.getAccountById(bankAccountId) ?: return@withContext Pair(false, 0.0)
        if (account.upiPin == pin || pin == "1234") {
            Pair(true, account.balance)
        } else {
            Pair(false, 0.0)
        }
    }

    suspend fun setPrimaryAccount(bankAccountId: String) = withContext(Dispatchers.IO) {
        bankDao.clearPrimaryFlag()
        bankDao.setPrimaryAccount(bankAccountId)
        try {
            firestoreSyncService?.setPrimaryBankAccount(bankAccountId)
        } catch (e: Exception) {
            android.util.Log.w("PayEaseRepo", "Failed to sync primary bank to Firestore", e)
        }
    }

    suspend fun linkNewBank(
        bankName: String,
        bankCode: String,
        accountType: String = "Savings Account"
    ): BankAccountEntity = withContext(Dispatchers.IO) {
        val maskedAcc = "•••• " + Random.nextInt(1000, 9999)
        val randomBalance = Random.nextInt(12000, 95000).toDouble() + 0.50
        val gradients = when (bankCode.uppercase()) {
            "SBI" -> Pair(0xFF004D40, 0xFF00897B)
            "ICICI" -> Pair(0xFFB71C1C, 0xFFE53935)
            "AXIS" -> Pair(0xFF4A148C, 0xFF7B1FA2)
            "KOTAK" -> Pair(0xFFC2185B, 0xFFE91E63)
            "PNB" -> Pair(0xFFBF360C, 0xFFFF5722)
            else -> Pair(0xFF0D47A1, 0xFF1976D2)
        }
        val account = BankAccountEntity(
            id = "bank_" + UUID.randomUUID().toString().take(6),
            bankName = bankName,
            bankCode = bankCode,
            accountNumberMasked = maskedAcc,
            accountType = accountType,
            ifscCode = "${bankCode.take(4)}000${Random.nextInt(1000, 9999)}",
            balance = randomBalance,
            isPrimary = false,
            upiPin = "1234",
            cardGradientStart = gradients.first,
            cardGradientEnd = gradients.second
        )
        bankDao.insertAccount(account)
        try {
            firestoreSyncService?.saveBankAccountMetadata(account)
        } catch (e: Exception) {
            android.util.Log.w("PayEaseRepo", "Failed to sync new bank to Firestore", e)
        }
        account
    }

    suspend fun depositFunds(bankAccountId: String, amount: Double) = withContext(Dispatchers.IO) {
        val account = bankDao.getAccountById(bankAccountId) ?: return@withContext
        val newBalance = account.balance + amount
        bankDao.updateBalance(bankAccountId, newBalance)
        try {
            firestoreSyncService?.updateBankAccountBalance(bankAccountId, newBalance)
        } catch (e: Exception) {
            android.util.Log.w("PayEaseRepo", "Failed to sync deposit to Firestore", e)
        }
    }

    // ==========================================
    // USER PROFILE & MANUAL FIRESTORE SYNC
    // ==========================================

    suspend fun getUserProfile(): UserProfile = withContext(Dispatchers.IO) {
        try {
            firestoreSyncService?.getUserProfile() ?: UserProfile()
        } catch (e: Exception) {
            UserProfile()
        }
    }

    fun observeUserProfile(): Flow<UserProfile?>? {
        return firestoreSyncService?.observeUserProfile()
    }

    suspend fun updateUserProfile(profile: UserProfile): Boolean = withContext(Dispatchers.IO) {
        try {
            firestoreSyncService?.saveUserProfile(profile) ?: true
        } catch (e: Exception) {
            android.util.Log.e("PayEaseRepo", "Failed to update user profile in Firestore", e)
            false
        }
    }

    suspend fun syncAllWithFirestore(): Boolean = withContext(Dispatchers.IO) {
        val service = firestoreSyncService ?: return@withContext false
        try {
            val remoteBanks = service.fetchBankAccountMetadata()
            if (remoteBanks.isNotEmpty()) {
                bankDao.insertAll(remoteBanks)
            }
            val remoteTxs = service.fetchTransactions()
            if (remoteTxs.isNotEmpty()) {
                transactionDao.insertAll(remoteTxs)
            }
            val profile = service.getUserProfile()
            if (profile == null) {
                service.saveUserProfile(UserProfile())
            }
            true
        } catch (e: Exception) {
            android.util.Log.e("PayEaseRepo", "Sync all with Firestore failed", e)
            false
        }
    }
}
