package com.example

import com.example.data.local.entity.TransactionEntity
import com.example.ui.DateFilterType
import com.example.ui.TypeFilterType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class TransactionFilterTest {

    private val now = System.currentTimeMillis()
    private val oneDayMs = 24 * 60 * 60 * 1000L

    private val sampleTransactions = listOf(
        TransactionEntity(
            id = "tx1",
            contactId = "c1",
            contactName = "Priya Sharma",
            contactVpa = "priya@okaxis",
            amount = 1200.0,
            type = "SENT",
            status = "SUCCESS",
            bankAccountId = "bank_hdfc",
            bankName = "HDFC Bank",
            upiRefId = "UPI982347101",
            timestamp = now - 2 * oneDayMs, // 2 days ago
            note = "Groceries split",
            locationTag = "Indiranagar, Bengaluru",
            cashbackWon = 15.0
        ),
        TransactionEntity(
            id = "tx2",
            contactId = "c2",
            contactName = "Rohan Verma",
            contactVpa = "rohan@okhdfcbank",
            amount = 450.0,
            type = "RECEIVED",
            status = "SUCCESS",
            bankAccountId = "bank_sbi",
            bankName = "State Bank of India",
            upiRefId = "UPI837194821",
            timestamp = now - 15 * oneDayMs, // 15 days ago
            note = "Coffee payment",
            locationTag = "Koramangala, Bengaluru",
            cashbackWon = 0.0
        ),
        TransactionEntity(
            id = "tx3",
            contactId = "c3",
            contactName = "Aarav Patel",
            contactVpa = "aarav@oksbi",
            amount = 3000.0,
            type = "SENT",
            status = "PENDING",
            bankAccountId = "bank_hdfc",
            bankName = "HDFC Bank",
            upiRefId = "UPI548192039",
            timestamp = now - 45 * oneDayMs, // 45 days ago
            note = "Electricity bill",
            locationTag = "HSR Layout, Bengaluru",
            cashbackWon = 0.0
        )
    )

    private fun filter(
        transactions: List<TransactionEntity>,
        dateFilter: DateFilterType,
        typeFilter: TypeFilterType,
        bankId: String?,
        query: String
    ): List<TransactionEntity> {
        val currentTime = now
        val cal = Calendar.getInstance().apply {
            timeInMillis = currentTime
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfMonth = cal.timeInMillis

        return transactions.filter { tx ->
            val dateMatches = when (dateFilter) {
                DateFilterType.ALL -> true
                DateFilterType.LAST_7_DAYS -> (currentTime - tx.timestamp) <= (7L * 24 * 60 * 60 * 1000L)
                DateFilterType.LAST_30_DAYS -> (currentTime - tx.timestamp) <= (30L * 24 * 60 * 60 * 1000L)
                DateFilterType.THIS_MONTH -> tx.timestamp >= startOfMonth
            }

            val typeMatches = when (typeFilter) {
                TypeFilterType.ALL -> true
                TypeFilterType.SENT -> tx.type == "SENT"
                TypeFilterType.RECEIVED -> tx.type == "RECEIVED"
                TypeFilterType.PENDING -> tx.status == "PENDING"
            }

            val bankMatches = if (bankId == null) true else tx.bankAccountId == bankId

            val queryMatches = if (query.isBlank()) {
                true
            } else {
                val q = query.trim().lowercase()
                tx.contactName.lowercase().contains(q) ||
                    tx.contactVpa.lowercase().contains(q) ||
                    tx.note.lowercase().contains(q) ||
                    tx.bankName.lowercase().contains(q) ||
                    tx.locationTag.lowercase().contains(q)
            }

            dateMatches && typeMatches && bankMatches && queryMatches
        }
    }

    @Test
    fun testDateFilter_last7Days() {
        val result = filter(sampleTransactions, DateFilterType.LAST_7_DAYS, TypeFilterType.ALL, null, "")
        assertEquals(1, result.size)
        assertEquals("tx1", result[0].id)
    }

    @Test
    fun testDateFilter_last30Days() {
        val result = filter(sampleTransactions, DateFilterType.LAST_30_DAYS, TypeFilterType.ALL, null, "")
        assertEquals(2, result.size)
        assertTrue(result.any { it.id == "tx1" })
        assertTrue(result.any { it.id == "tx2" })
    }

    @Test
    fun testTypeFilter_received() {
        val result = filter(sampleTransactions, DateFilterType.ALL, TypeFilterType.RECEIVED, null, "")
        assertEquals(1, result.size)
        assertEquals("tx2", result[0].id)
    }

    @Test
    fun testTypeFilter_pending() {
        val result = filter(sampleTransactions, DateFilterType.ALL, TypeFilterType.PENDING, null, "")
        assertEquals(1, result.size)
        assertEquals("tx3", result[0].id)
    }

    @Test
    fun testBankFilter_hdfc() {
        val result = filter(sampleTransactions, DateFilterType.ALL, TypeFilterType.ALL, "bank_hdfc", "")
        assertEquals(2, result.size)
        assertTrue(result.all { it.bankAccountId == "bank_hdfc" })
    }

    @Test
    fun testCombinedFilters_sentAndHdfcAndLast7Days() {
        val result = filter(sampleTransactions, DateFilterType.LAST_7_DAYS, TypeFilterType.SENT, "bank_hdfc", "")
        assertEquals(1, result.size)
        assertEquals("tx1", result[0].id)
    }

    @Test
    fun testSearchQuery() {
        val result = filter(sampleTransactions, DateFilterType.ALL, TypeFilterType.ALL, null, "Koramangala")
        assertEquals(1, result.size)
        assertEquals("tx2", result[0].id)
    }
}
