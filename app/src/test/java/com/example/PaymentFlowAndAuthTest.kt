package com.example

import com.example.data.local.entity.BankAccountEntity
import com.example.data.local.entity.RewardEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.remote.firestore.UserProfile
import com.example.ui.PendingPaymentAuth
import com.example.ui.components.InsufficientBalanceData
import com.example.ui.components.PaymentFailureData
import com.example.ui.PaymentSuccessData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PaymentFlowAndAuthTest {

    @Test
    fun testUserProfileDefaultsAndCustomAvatar() {
        val defaultProfile = UserProfile()
        assertEquals("Alex Morgan", defaultProfile.displayName)
        assertEquals("PRESET_3D_MALE", defaultProfile.avatarType)
        assertEquals("🚀", defaultProfile.avatarEmoji)

        val customProfile = defaultProfile.copy(
            displayName = "Zain Taqi",
            email = "zaintaqi85@gmail.com",
            avatarType = "PRESET_EMOJI",
            avatarEmoji = "⚡",
            avatarBgColor = 0xFFE11D48
        )

        assertEquals("Zain Taqi", customProfile.displayName)
        assertEquals("zaintaqi85@gmail.com", customProfile.email)
        assertEquals("PRESET_EMOJI", customProfile.avatarType)
        assertEquals("⚡", customProfile.avatarEmoji)
        assertEquals(0xFFE11D48, customProfile.avatarBgColor)

        // Serialization map test
        val map = customProfile.toMap()
        assertEquals("Zain Taqi", map["displayName"])
        assertEquals("PRESET_EMOJI", map["avatarType"])
        assertEquals("⚡", map["avatarEmoji"])

        val reconstructed = UserProfile.fromMap(map)
        assertEquals(customProfile.displayName, reconstructed.displayName)
        assertEquals(customProfile.avatarType, reconstructed.avatarType)
        assertEquals(customProfile.avatarEmoji, reconstructed.avatarEmoji)
    }

    @Test
    fun testInsufficientBalanceDetection() {
        val testBank = BankAccountEntity(
            id = "bank_sbi",
            bankName = "State Bank of India",
            bankCode = "SBI",
            accountNumberMasked = "•••• 9012",
            accountType = "Savings Account",
            ifscCode = "SBIN0004567",
            balance = 1500.00,
            isPrimary = true,
            upiPin = "1234",
            cardGradientStart = 0xFF004D40,
            cardGradientEnd = 0xFF00897B
        )

        val attemptedAmount = 5000.0
        val isInsufficient = attemptedAmount > testBank.balance
        assertTrue("Attempted amount should exceed available balance", isInsufficient)

        val deficit = attemptedAmount - testBank.balance
        assertEquals(3500.0, deficit, 0.001)

        val pending = PendingPaymentAuth(
            recipientContactId = "c_priya",
            recipientName = "Priya Sharma",
            recipientVpa = "priya.sharma@okaxis",
            amount = attemptedAmount,
            bankAccountId = testBank.id,
            note = "Rent split"
        )

        val insufficientData = InsufficientBalanceData(
            attemptedAmount = attemptedAmount,
            availableBalance = testBank.balance,
            deficit = deficit,
            bankAccount = testBank,
            recipientName = "Priya Sharma",
            pendingPayment = pending
        )

        assertEquals(3500.0, insufficientData.deficit, 0.001)
        assertEquals("State Bank of India", insufficientData.bankAccount.bankName)
    }

    @Test
    fun testPaymentFailureDataModel() {
        val failureData = PaymentFailureData(
            recipientName = "Sneha Patel",
            recipientVpa = "sneha.patel@icici",
            amount = 1200.0,
            bankAccount = null,
            reason = "NPCI switch timeout (Error 91)",
            errorCode = "U30-DECLINED",
            canRetry = true
        )

        assertEquals("Sneha Patel", failureData.recipientName)
        assertEquals(1200.0, failureData.amount, 0.001)
        assertEquals("U30-DECLINED", failureData.errorCode)
        assertTrue(failureData.canRetry)
    }

    @Test
    fun testPaymentSuccessDataModel() {
        val tx = TransactionEntity(
            id = "TX12345678",
            contactId = "c_rahul",
            contactName = "Rahul Verma",
            contactVpa = "rahul.v@okhdfcbank",
            amount = 450.0,
            type = "SENT",
            status = "SUCCESS",
            bankAccountId = "bank_hdfc",
            bankName = "HDFC Bank",
            upiRefId = "UPI/98127391823/Rahul",
            timestamp = System.currentTimeMillis(),
            note = "Dinner split",
            cashbackWon = 35.0
        )

        val reward = RewardEntity(
            id = "rew_9912",
            transactionId = tx.id,
            amount = 35.0,
            isScratched = false,
            timestamp = System.currentTimeMillis(),
            title = "Instant Payment Cashback",
            description = "Guaranteed reward for ₹450 transfer"
        )

        val successData = PaymentSuccessData(
            transaction = tx,
            reward = reward,
            bankAccount = null
        )

        assertEquals("Rahul Verma", successData.transaction.contactName)
        assertEquals(450.0, successData.transaction.amount, 0.001)
        assertNotNull(successData.reward)
        assertEquals(35.0, successData.reward?.amount ?: 0.0, 0.001)
    }
}
