package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.BankAccountEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ContactEntity
import com.example.data.local.entity.RewardEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.location.LocationHelper
import com.example.data.remote.firestore.FirestoreSyncService
import com.example.data.remote.firestore.UserProfile
import com.example.data.repository.PayEaseRepository
import com.example.security.BiometricAuthManager
import com.example.security.BiometricResult
import com.example.security.SecurityPreferences
import androidx.fragment.app.FragmentActivity
import com.example.ui.components.InsufficientBalanceData
import com.example.ui.components.PaymentFailureData
import com.example.ui.components.PaymentResultStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class PaymentSuccessData(
    val transaction: TransactionEntity,
    val reward: RewardEntity?,
    val bankAccount: BankAccountEntity?
)

enum class DateFilterType(val label: String) {
    ALL("All Time"),
    LAST_7_DAYS("Last 7 Days"),
    LAST_30_DAYS("Last 30 Days"),
    THIS_MONTH("This Month")
}

enum class TypeFilterType(val label: String) {
    ALL("All Types"),
    SENT("Sent"),
    RECEIVED("Received"),
    PENDING("Pending")
}

data class DailyLimitExceededInfo(
    val attemptedAmount: Double,
    val todaySpent: Double,
    val dailyLimit: Double,
    val excessAmount: Double,
    val recipientName: String,
    val pendingPayment: PendingPaymentAuth
)

data class PendingPaymentAuth(
    val recipientContactId: String?,
    val recipientName: String,
    val recipientVpa: String,
    val amount: Double,
    val bankAccountId: String,
    val note: String
)

data class ProcessingPaymentInfo(
    val payment: PendingPaymentAuth,
    val bankName: String
)

data class ActivePaymentResult(
    val status: PaymentResultStatus,
    val payment: PendingPaymentAuth,
    val bankName: String,
    val transaction: TransactionEntity? = null,
    val reward: RewardEntity? = null,
    val errorMessage: String? = null
)

class PayEaseViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val locationHelper = LocationHelper(application)
    private val firestoreSyncService = FirestoreSyncService(application)
    private val repository = PayEaseRepository(database, locationHelper, firestoreSyncService)
    private val securityPrefs = SecurityPreferences(application)

    // Data streams from repository
    val bankAccounts: StateFlow<List<BankAccountEntity>> = repository.allBankAccounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val contacts: StateFlow<List<ContactEntity>> = repository.allContacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rewards: StateFlow<List<RewardEntity>> = repository.allRewards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCashbackWon: StateFlow<Double?> = repository.totalCashbackWon
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val unclaimedRewardsCount: StateFlow<Int> = repository.unclaimedRewardsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Transaction Filters
    val selectedDateFilter = MutableStateFlow(DateFilterType.ALL)
    val selectedTypeFilter = MutableStateFlow(TypeFilterType.ALL)
    val selectedBankFilterId = MutableStateFlow<String?>(null) // null = all banks
    val searchQuery = MutableStateFlow("")

    // Filtered Transactions
    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        repository.allTransactions,
        selectedDateFilter,
        selectedTypeFilter,
        selectedBankFilterId,
        searchQuery
    ) { txs, dateFilter, typeFilter, bankId, query ->
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance()

        txs.filter { tx ->
            // Date filter
            val matchesDate = when (dateFilter) {
                DateFilterType.ALL -> true
                DateFilterType.LAST_7_DAYS -> (now - tx.timestamp) <= 7L * 24 * 60 * 60 * 1000
                DateFilterType.LAST_30_DAYS -> (now - tx.timestamp) <= 30L * 24 * 60 * 60 * 1000
                DateFilterType.THIS_MONTH -> {
                    cal.timeInMillis = now
                    val currentMonth = cal.get(Calendar.MONTH)
                    val currentYear = cal.get(Calendar.YEAR)
                    cal.timeInMillis = tx.timestamp
                    cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear
                }
            }

            // Type filter
            val matchesType = when (typeFilter) {
                TypeFilterType.ALL -> true
                TypeFilterType.SENT -> tx.type == "SENT"
                TypeFilterType.RECEIVED -> tx.type == "RECEIVED"
                TypeFilterType.PENDING -> tx.status == "PENDING" || tx.type == "REQUESTED"
            }

            // Bank Account filter
            val matchesBank = bankId == null || tx.bankAccountId == bankId

            // Search query filter
            val matchesQuery = if (query.isBlank()) true else {
                tx.contactName.contains(query, ignoreCase = true) ||
                        tx.contactVpa.contains(query, ignoreCase = true) ||
                        tx.upiRefId.contains(query, ignoreCase = true) ||
                        tx.bankName.contains(query, ignoreCase = true) ||
                        tx.note.contains(query, ignoreCase = true)
            }

            matchesDate && matchesType && matchesBank && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Chat state
    private val _activeContact = MutableStateFlow<ContactEntity?>(null)
    val activeContact: StateFlow<ContactEntity?> = _activeContact.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessageEntity>> = _chatMessages.asStateFlow()

    // Biometric & Security States
    private val _isAppUnlocked = MutableStateFlow(false)
    val isAppUnlocked: StateFlow<Boolean> = _isAppUnlocked.asStateFlow()

    private val _isBiometricAppLockEnabled = MutableStateFlow(securityPrefs.isBiometricAppLockEnabled)
    val isBiometricAppLockEnabled: StateFlow<Boolean> = _isBiometricAppLockEnabled.asStateFlow()

    private val _isBiometricPaymentAuthEnabled = MutableStateFlow(securityPrefs.isBiometricPaymentAuthEnabled)
    val isBiometricPaymentAuthEnabled: StateFlow<Boolean> = _isBiometricPaymentAuthEnabled.asStateFlow()

    private val _isBiometricBalanceAuthEnabled = MutableStateFlow(securityPrefs.isBiometricBalanceAuthEnabled)
    val isBiometricBalanceAuthEnabled: StateFlow<Boolean> = _isBiometricBalanceAuthEnabled.asStateFlow()

    // Daily Transaction Limit State
    private val _isDailyLimitEnabled = MutableStateFlow(securityPrefs.isDailyLimitEnabled)
    val isDailyLimitEnabled: StateFlow<Boolean> = _isDailyLimitEnabled.asStateFlow()

    private val _dailyTransactionLimit = MutableStateFlow(securityPrefs.dailyTransactionLimit)
    val dailyTransactionLimit: StateFlow<Double> = _dailyTransactionLimit.asStateFlow()

    // Real-time calculation of today's total spent amount
    val todaySpentAmount: StateFlow<Double> = repository.allTransactions.map { txList ->
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        txList.filter { it.type == "SENT" && it.status == "SUCCESS" && it.timestamp >= startOfDay }
            .sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val remainingDailyLimit: StateFlow<Double> = combine(todaySpentAmount, _dailyTransactionLimit, _isDailyLimitEnabled) { spent, limit, enabled ->
        if (!enabled) Double.MAX_VALUE else (limit - spent).coerceAtLeast(0.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), securityPrefs.dailyTransactionLimit)

    val dailyLimitProgress: StateFlow<Float> = combine(todaySpentAmount, _dailyTransactionLimit) { spent, limit ->
        if (limit > 0) (spent / limit).coerceIn(0.0, 1.0).toFloat() else 0f
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    private val _dailyLimitExceeded = MutableStateFlow<DailyLimitExceededInfo?>(null)
    val dailyLimitExceeded: StateFlow<DailyLimitExceededInfo?> = _dailyLimitExceeded.asStateFlow()

    private val _showSetDailyLimitDialog = MutableStateFlow(false)
    val showSetDailyLimitDialog: StateFlow<Boolean> = _showSetDailyLimitDialog.asStateFlow()

    private val _showSecuritySettingsDialog = MutableStateFlow(false)
    val showSecuritySettingsDialog: StateFlow<Boolean> = _showSecuritySettingsDialog.asStateFlow()

    private val _showAppLockPinFallback = MutableStateFlow(false)
    val showAppLockPinFallback: StateFlow<Boolean> = _showAppLockPinFallback.asStateFlow()

    private val _appLockPinError = MutableStateFlow<String?>(null)
    val appLockPinError: StateFlow<String?> = _appLockPinError.asStateFlow()

    // Sensitive Payment Authorization state
    private val _pendingPaymentAuth = MutableStateFlow<PendingPaymentAuth?>(null)
    val pendingPaymentAuth: StateFlow<PendingPaymentAuth?> = _pendingPaymentAuth.asStateFlow()

    private val _paymentPinErrorMessage = MutableStateFlow<String?>(null)
    val paymentPinErrorMessage: StateFlow<String?> = _paymentPinErrorMessage.asStateFlow()

    // Balance check state
    private val _balanceCheckingAccount = MutableStateFlow<BankAccountEntity?>(null)
    val balanceCheckingAccount: StateFlow<BankAccountEntity?> = _balanceCheckingAccount.asStateFlow()

    private val _revealedBalanceMap = MutableStateFlow<Map<String, Double>>(emptyMap())
    val revealedBalanceMap: StateFlow<Map<String, Double>> = _revealedBalanceMap.asStateFlow()

    private val _pinErrorMessage = MutableStateFlow<String?>(null)
    val pinErrorMessage: StateFlow<String?> = _pinErrorMessage.asStateFlow()

    // Active celebratory reward dialog
    private val _activeCelebrationReward = MutableStateFlow<RewardEntity?>(null)
    val activeCelebrationReward: StateFlow<RewardEntity?> = _activeCelebrationReward.asStateFlow()

    // Payment Result Animations State
    private val _paymentSuccessData = MutableStateFlow<PaymentSuccessData?>(null)
    val paymentSuccessData: StateFlow<PaymentSuccessData?> = _paymentSuccessData.asStateFlow()

    private val _paymentFailureData = MutableStateFlow<PaymentFailureData?>(null)
    val paymentFailureData: StateFlow<PaymentFailureData?> = _paymentFailureData.asStateFlow()

    private val _processingPayment = MutableStateFlow<ProcessingPaymentInfo?>(null)
    val processingPayment: StateFlow<ProcessingPaymentInfo?> = _processingPayment.asStateFlow()

    private val _activePaymentResult = MutableStateFlow<ActivePaymentResult?>(null)
    val activePaymentResult: StateFlow<ActivePaymentResult?> = _activePaymentResult.asStateFlow()

    private val _lastFailedPayment = MutableStateFlow<PendingPaymentAuth?>(null)

    private val _insufficientBalanceData = MutableStateFlow<InsufficientBalanceData?>(null)
    val insufficientBalanceData: StateFlow<InsufficientBalanceData?> = _insufficientBalanceData.asStateFlow()

    // User Login / Logout state
    private val _isUserLoggedIn = MutableStateFlow(true)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()

    private val _showLoginDialog = MutableStateFlow(false)
    val showLoginDialog: StateFlow<Boolean> = _showLoginDialog.asStateFlow()

    // QR & Payment state
    private val _scannedUpiPayload = MutableStateFlow<String?>(null)
    val scannedUpiPayload: StateFlow<String?> = _scannedUpiPayload.asStateFlow()

    // User Profile persistent in Firestore
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Dynamic user name and VPA derived from persistent profile
    val userVpa: String get() = _userProfile.value.vpa
    val userName: String get() = _userProfile.value.displayName

    // Firestore Sync state
    private val _isCloudSyncing = MutableStateFlow(false)
    val isCloudSyncing: StateFlow<Boolean> = _isCloudSyncing.asStateFlow()

    private val _cloudSyncStatusMessage = MutableStateFlow<String?>("Cloud synced with Firestore")
    val cloudSyncStatusMessage: StateFlow<String?> = _cloudSyncStatusMessage.asStateFlow()

    private val _showProfileDialog = MutableStateFlow(false)
    val showProfileDialog: StateFlow<Boolean> = _showProfileDialog.asStateFlow()

    init {
        viewModelScope.launch {
            _isCloudSyncing.value = true
            repository.initializeSeedDataIfNeeded()
            val profile = repository.getUserProfile()
            _userProfile.value = profile
            _isCloudSyncing.value = false
        }
        viewModelScope.launch {
            repository.observeUserProfile()?.collect { profile ->
                if (profile != null) {
                    _userProfile.value = profile
                }
            }
        }
    }

    fun openChat(contact: ContactEntity) {
        _activeContact.value = contact
        viewModelScope.launch {
            repository.getMessagesForContact(contact.id).collect { messages ->
                _chatMessages.value = messages
            }
        }
    }

    fun closeChat() {
        _activeContact.value = null
        _chatMessages.value = emptyList()
    }

    fun sendChatMessage(text: String, attachmentUri: Uri? = null) {
        val contact = _activeContact.value ?: return
        if (text.isBlank() && attachmentUri == null) return
        viewModelScope.launch {
            repository.sendTextMessage(contact.id, text, attachmentUri?.toString())
        }
    }

    fun makePayment(
        recipientContactId: String?,
        recipientName: String,
        recipientVpa: String,
        amount: Double,
        bankAccountId: String,
        note: String
    ) {
        val payment = PendingPaymentAuth(
            recipientContactId = recipientContactId,
            recipientName = recipientName,
            recipientVpa = recipientVpa,
            amount = amount,
            bankAccountId = bankAccountId,
            note = note
        )
        executePayment(payment)
    }

    fun requestMoneyFromContact(contactId: String, amount: Double, note: String) {
        viewModelScope.launch {
            repository.requestMoney(contactId, amount, note)
        }
    }

    fun simulateIncomingTransfer(contactId: String, amount: Double, note: String) {
        viewModelScope.launch {
            repository.simulateIncomingMoney(contactId, amount, note)
        }
    }

    fun startBalanceCheck(account: BankAccountEntity) {
        _pinErrorMessage.value = null
        _balanceCheckingAccount.value = account
    }

    fun cancelBalanceCheck() {
        _balanceCheckingAccount.value = null
        _pinErrorMessage.value = null
    }

    fun verifyPinAndShowBalance(pin: String) {
        val account = _balanceCheckingAccount.value ?: return
        viewModelScope.launch {
            val (success, balance) = repository.checkUpiPin(account.id, pin)
            if (success) {
                _revealedBalanceMap.value = _revealedBalanceMap.value + (account.id to balance)
                _balanceCheckingAccount.value = null
                _pinErrorMessage.value = null
            } else {
                _pinErrorMessage.value = "Incorrect UPI PIN. Default PIN is 1234."
            }
        }
    }

    fun dismissCelebration() {
        _activeCelebrationReward.value = null
    }

    fun claimCelebrationReward(amount: Double) {
        val reward = _activeCelebrationReward.value ?: return
        viewModelScope.launch {
            repository.claimCashback(reward.id, amount)
        }
    }

    fun setPrimaryBank(bankAccountId: String) {
        viewModelScope.launch {
            repository.setPrimaryAccount(bankAccountId)
        }
    }

    fun linkNewBankAccount(bankName: String, bankCode: String, type: String = "Savings Account") {
        viewModelScope.launch {
            repository.linkNewBank(bankName, bankCode, type)
        }
    }

    fun onQrScanned(payload: String) {
        _scannedUpiPayload.value = payload
    }

    fun clearScannedQr() {
        _scannedUpiPayload.value = null
    }

    // Filter controls
    fun setDateFilter(filter: DateFilterType) {
        selectedDateFilter.value = filter
    }

    fun setTypeFilter(filter: TypeFilterType) {
        selectedTypeFilter.value = filter
    }

    fun setBankFilter(bankId: String?) {
        selectedBankFilterId.value = bankId
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun resetAllFilters() {
        selectedDateFilter.value = DateFilterType.ALL
        selectedTypeFilter.value = TypeFilterType.ALL
        selectedBankFilterId.value = null
        searchQuery.value = ""
    }

    // Biometric Security Controls
    fun unlockAppWithBiometrics() {
        _isAppUnlocked.value = true
        _showAppLockPinFallback.value = false
        _appLockPinError.value = null
    }

    fun showPinFallback() {
        _appLockPinError.value = null
        _showAppLockPinFallback.value = true
    }

    fun dismissPinFallback() {
        _showAppLockPinFallback.value = false
        _appLockPinError.value = null
    }

    fun unlockAppWithPin(pin: String): Boolean {
        return if (pin == "1234") {
            _isAppUnlocked.value = true
            _showAppLockPinFallback.value = false
            _appLockPinError.value = null
            true
        } else {
            _appLockPinError.value = "Incorrect UPI PIN. Default PIN is 1234."
            false
        }
    }

    fun lockApp() {
        _isAppUnlocked.value = false
        _showAppLockPinFallback.value = false
        _appLockPinError.value = null
    }

    fun openSecuritySettings() {
        _showSecuritySettingsDialog.value = true
    }

    fun closeSecuritySettings() {
        _showSecuritySettingsDialog.value = false
    }

    fun toggleBiometricAppLock(enabled: Boolean) {
        securityPrefs.isBiometricAppLockEnabled = enabled
        _isBiometricAppLockEnabled.value = enabled
        if (!enabled) {
            _isAppUnlocked.value = true
        }
    }

    fun toggleBiometricPaymentAuth(enabled: Boolean) {
        securityPrefs.isBiometricPaymentAuthEnabled = enabled
        _isBiometricPaymentAuthEnabled.value = enabled
    }

    fun toggleBiometricBalanceAuth(enabled: Boolean) {
        securityPrefs.isBiometricBalanceAuthEnabled = enabled
        _isBiometricBalanceAuthEnabled.value = enabled
    }

    // Daily Transaction Limit Actions
    fun toggleDailyLimit(enabled: Boolean) {
        securityPrefs.isDailyLimitEnabled = enabled
        _isDailyLimitEnabled.value = enabled
    }

    fun updateDailyLimit(newLimit: Double) {
        securityPrefs.dailyTransactionLimit = newLimit
        _dailyTransactionLimit.value = newLimit
        _showSetDailyLimitDialog.value = false
        _dailyLimitExceeded.value = null
    }

    fun openSetDailyLimitDialog() {
        _showSetDailyLimitDialog.value = true
    }

    fun closeSetDailyLimitDialog() {
        _showSetDailyLimitDialog.value = false
    }

    fun dismissDailyLimitExceeded() {
        _dailyLimitExceeded.value = null
    }

    fun directRevealBalance(accountId: String) {
        viewModelScope.launch {
            val account = bankAccounts.value.firstOrNull { it.id == accountId }
            if (account != null) {
                _revealedBalanceMap.value = _revealedBalanceMap.value + (account.id to account.balance)
            }
        }
    }

    // Sensitive Transaction Action Authorization
    fun initiatePayment(payment: PendingPaymentAuth, activity: FragmentActivity?) {
        // 1. Enforce daily transaction limit
        if (_isDailyLimitEnabled.value) {
            val spentSoFar = todaySpentAmount.value
            val limit = _dailyTransactionLimit.value
            if (spentSoFar + payment.amount > limit) {
                _dailyLimitExceeded.value = DailyLimitExceededInfo(
                    attemptedAmount = payment.amount,
                    todaySpent = spentSoFar,
                    dailyLimit = limit,
                    excessAmount = (spentSoFar + payment.amount) - limit,
                    recipientName = payment.recipientName,
                    pendingPayment = payment
                )
                return
            }
        }

        // 2. Enforce insufficient balance check
        val selectedBank = bankAccounts.value.firstOrNull { it.id == payment.bankAccountId }
            ?: bankAccounts.value.firstOrNull { it.isPrimary }
            ?: bankAccounts.value.firstOrNull()

        if (selectedBank != null && selectedBank.balance < payment.amount) {
            _insufficientBalanceData.value = InsufficientBalanceData(
                attemptedAmount = payment.amount,
                availableBalance = selectedBank.balance,
                deficit = payment.amount - selectedBank.balance,
                bankAccount = selectedBank,
                recipientName = payment.recipientName,
                pendingPayment = payment
            )
            return
        }

        // 3. Simulated or requested failure
        if (payment.note.contains("FAIL", ignoreCase = true) || payment.note.contains("DECLINE", ignoreCase = true)) {
            _lastFailedPayment.value = payment
            _paymentFailureData.value = PaymentFailureData(
                recipientName = payment.recipientName,
                recipientVpa = payment.recipientVpa,
                amount = payment.amount,
                bankAccount = selectedBank,
                reason = "Bank server response timeout (NPCI error 91). Transaction declined by issuing bank.",
                errorCode = "U30-DECLINED",
                canRetry = true
            )
            return
        }

        if (_isBiometricPaymentAuthEnabled.value && activity != null) {
            BiometricAuthManager.authenticate(
                activity = activity,
                title = "Authorize UPI Payment",
                subtitle = "Pay ₹${payment.amount} to ${payment.recipientName}",
                description = "Confirm with your fingerprint/face to securely authorize this payment",
                negativeButtonText = "Use UPI PIN",
                onResult = { result ->
                    when (result) {
                        is BiometricResult.Success -> {
                            executePayment(payment)
                        }
                        is BiometricResult.FallbackToPin, is BiometricResult.Error -> {
                            _paymentPinErrorMessage.value = null
                            _pendingPaymentAuth.value = payment
                        }
                        is BiometricResult.Failed -> {
                            // Let user retry biometric sensor
                        }
                    }
                }
            )
        } else {
            // Require UPI PIN verification
            _paymentPinErrorMessage.value = null
            _pendingPaymentAuth.value = payment
        }
    }

    fun dismissPaymentSuccess() {
        _paymentSuccessData.value = null
    }

    fun claimRewardFromSuccess(reward: RewardEntity) {
        _paymentSuccessData.value = null
        _activeCelebrationReward.value = reward
    }

    fun dismissPaymentFailure() {
        _paymentFailureData.value = null
    }

    fun retryFailedPayment(activity: FragmentActivity?) {
        val last = _lastFailedPayment.value ?: return
        _paymentFailureData.value = null
        initiatePayment(last.copy(note = "Payment Transfer"), activity)
    }

    fun dismissInsufficientBalance() {
        _insufficientBalanceData.value = null
    }

    fun switchBankAndPay(newBank: BankAccountEntity, activity: FragmentActivity?) {
        val oldData = _insufficientBalanceData.value ?: return
        _insufficientBalanceData.value = null
        val updatedPayment = oldData.pendingPayment.copy(bankAccountId = newBank.id)
        initiatePayment(updatedPayment, activity)
    }

    fun depositAndRetryPayment(amountToAdd: Double, activity: FragmentActivity?) {
        val oldData = _insufficientBalanceData.value ?: return
        viewModelScope.launch {
            repository.depositFunds(oldData.bankAccount.id, amountToAdd)
            _insufficientBalanceData.value = null
            initiatePayment(oldData.pendingPayment, activity)
        }
    }

    fun simulatePaymentFailureTest() {
        val primary = bankAccounts.value.firstOrNull { it.isPrimary } ?: bankAccounts.value.firstOrNull()
        _lastFailedPayment.value = PendingPaymentAuth(
            recipientContactId = "c_priya",
            recipientName = "Priya Sharma",
            recipientVpa = "priya.sharma@okaxis",
            amount = 850.0,
            bankAccountId = primary?.id ?: "",
            note = "Payment Transfer"
        )
        _paymentFailureData.value = PaymentFailureData(
            recipientName = "Priya Sharma",
            recipientVpa = "priya.sharma@okaxis",
            amount = 850.0,
            bankAccount = primary,
            reason = "NPCI UPI Switch Gateway timeout: issuing bank did not respond within 30s.",
            errorCode = "NPCI-91-TIMEOUT",
            canRetry = true
        )
    }

    fun simulateInsufficientBalanceTest() {
        val primary = bankAccounts.value.firstOrNull { it.isPrimary } ?: bankAccounts.value.firstOrNull()
        val dummyBank = primary ?: BankAccountEntity(
            id = "bank_hdfc",
            bankName = "HDFC Bank",
            bankCode = "HDFC",
            accountNumberMasked = "•••• 4821",
            accountType = "Savings Account",
            ifscCode = "HDFC0001234",
            balance = 120.0,
            isPrimary = true,
            upiPin = "1234",
            cardGradientStart = 0xFF0D47A1,
            cardGradientEnd = 0xFF1976D2
        )
        _insufficientBalanceData.value = InsufficientBalanceData(
            attemptedAmount = 50000.0,
            availableBalance = dummyBank.balance,
            deficit = (50000.0 - dummyBank.balance).coerceAtLeast(100.0),
            bankAccount = dummyBank,
            recipientName = "Rahul Verma",
            pendingPayment = PendingPaymentAuth(
                recipientContactId = "c_rahul",
                recipientName = "Rahul Verma",
                recipientVpa = "rahul.v@okhdfcbank",
                amount = 50000.0,
                bankAccountId = dummyBank.id,
                note = "Test Insufficient Balance"
            )
        )
    }

    // Login & Logout & Profile Avatar Controls
    fun logout() {
        _isUserLoggedIn.value = false
        _showProfileDialog.value = false
        _cloudSyncStatusMessage.value = "User logged out"
    }

    fun login(profile: UserProfile? = null) {
        if (profile != null) {
            _userProfile.value = profile
            viewModelScope.launch {
                repository.updateUserProfile(profile)
            }
        }
        _isUserLoggedIn.value = true
        _showLoginDialog.value = false
        _cloudSyncStatusMessage.value = "Logged in as ${_userProfile.value.displayName}"
    }

    fun openLoginDialog() {
        _showLoginDialog.value = true
    }

    fun dismissLoginDialog() {
        _showLoginDialog.value = false
    }

    fun updateAvatar(avatarType: String, avatarEmoji: String, avatarBgColor: Long) {
        val updated = _userProfile.value.copy(
            avatarType = avatarType,
            avatarEmoji = avatarEmoji,
            avatarBgColor = avatarBgColor,
            updatedAt = System.currentTimeMillis()
        )
        _userProfile.value = updated
        viewModelScope.launch {
            repository.updateUserProfile(updated)
        }
    }

    fun verifyPaymentPinAndExecute(pin: String) {
        val payment = _pendingPaymentAuth.value ?: return
        if (pin == "1234") {
            _paymentPinErrorMessage.value = null
            _pendingPaymentAuth.value = null
            executePayment(payment)
        } else {
            _paymentPinErrorMessage.value = "Incorrect UPI PIN. Default PIN is 1234."
        }
    }

    fun cancelPaymentPinAuth() {
        _pendingPaymentAuth.value = null
        _paymentPinErrorMessage.value = null
    }

    private fun executePayment(payment: PendingPaymentAuth) {
        val selectedBank = bankAccounts.value.firstOrNull { it.id == payment.bankAccountId }
            ?: bankAccounts.value.firstOrNull { it.isPrimary }
            ?: bankAccounts.value.firstOrNull()

        val bankName = selectedBank?.bankName ?: "Bank"

        // 1. Initial In-Flight State: PROCESSING
        _activePaymentResult.value = ActivePaymentResult(
            status = PaymentResultStatus.PROCESSING,
            payment = payment,
            bankName = bankName
        )
        _processingPayment.value = ProcessingPaymentInfo(
            payment = payment,
            bankName = bankName
        )

        viewModelScope.launch {
            // Realistic bank & NPCI switch clearing delay
            kotlinx.coroutines.delay(1400)
            _processingPayment.value = null

            // 2. Check if payment was marked to fail
            if (payment.note.contains("FAIL", ignoreCase = true) || payment.note.contains("DECLINE", ignoreCase = true)) {
                _lastFailedPayment.value = payment
                val reason = "Bank server response timeout (NPCI error 91). Transaction declined by issuing bank."
                _paymentFailureData.value = PaymentFailureData(
                    recipientName = payment.recipientName,
                    recipientVpa = payment.recipientVpa,
                    amount = payment.amount,
                    bankAccount = selectedBank,
                    reason = reason,
                    errorCode = "U30-DECLINED",
                    canRetry = true
                )
                // Morph to FAILED state
                _activePaymentResult.value = _activePaymentResult.value?.copy(
                    status = PaymentResultStatus.FAILED,
                    errorMessage = reason
                )
            } else {
                // 3. Success! Morph to SUCCESS state
                val (tx, reward) = repository.sendPayment(
                    contactId = payment.recipientContactId,
                    recipientName = payment.recipientName,
                    recipientVpa = payment.recipientVpa,
                    amount = payment.amount,
                    bankAccountId = selectedBank?.id ?: payment.bankAccountId,
                    note = payment.note
                )
                _paymentSuccessData.value = PaymentSuccessData(
                    transaction = tx,
                    reward = reward,
                    bankAccount = selectedBank
                )
                _activePaymentResult.value = _activePaymentResult.value?.copy(
                    status = PaymentResultStatus.SUCCESS,
                    transaction = tx,
                    reward = reward
                )
            }
        }
    }

    fun dismissPaymentResult() {
        _activePaymentResult.value = null
    }

    fun retryActivePaymentResult(activity: FragmentActivity?) {
        val active = _activePaymentResult.value ?: return
        _activePaymentResult.value = null
        initiatePayment(active.payment.copy(note = "Payment Transfer"), activity)
    }

    fun initiateBalanceCheck(account: BankAccountEntity, activity: FragmentActivity?) {
        if (_isBiometricBalanceAuthEnabled.value && activity != null) {
            BiometricAuthManager.authenticate(
                activity = activity,
                title = "Verify Identity",
                subtitle = "Check balance for ${account.bankName} ${account.accountNumberMasked}",
                description = "Bank-grade biometric authentication to access live balance",
                negativeButtonText = "Use UPI PIN",
                onResult = { result ->
                    when (result) {
                        is BiometricResult.Success -> {
                            directRevealBalance(account.id)
                        }
                        is BiometricResult.FallbackToPin, is BiometricResult.Error -> {
                            startBalanceCheck(account)
                        }
                        is BiometricResult.Failed -> {}
                    }
                }
            )
        } else {
            startBalanceCheck(account)
        }
    }

    // Profile & Firestore Sync Methods
    fun setShowProfileDialog(show: Boolean) {
        _showProfileDialog.value = show
    }

    fun updateUserProfile(
        displayName: String,
        vpa: String,
        phoneNumber: String,
        email: String = _userProfile.value.email
    ) {
        val updated = _userProfile.value.copy(
            displayName = displayName,
            vpa = vpa,
            phoneNumber = phoneNumber,
            email = email,
            updatedAt = System.currentTimeMillis()
        )
        _userProfile.value = updated
        viewModelScope.launch {
            _isCloudSyncing.value = true
            repository.updateUserProfile(updated)
            _isCloudSyncing.value = false
            _cloudSyncStatusMessage.value = "Profile synced to Firestore"
        }
    }

    fun syncAllWithCloud() {
        viewModelScope.launch {
            _isCloudSyncing.value = true
            val success = repository.syncAllWithFirestore()
            _isCloudSyncing.value = false
            _cloudSyncStatusMessage.value = if (success) "All data synced with Firestore" else "Sync failed (offline)"
        }
    }

    fun clearCloudSyncMessage() {
        _cloudSyncStatusMessage.value = null
    }
}


