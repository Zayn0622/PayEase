package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val contactId: String? = null,
    val contactName: String,
    val contactVpa: String,
    val amount: Double,
    val type: String, // "SENT", "RECEIVED", "REQUESTED"
    val status: String, // "SUCCESS", "PENDING", "FAILED"
    val timestamp: Long = System.currentTimeMillis(),
    val upiRefId: String,
    val bankAccountId: String,
    val bankName: String,
    val locationTag: String = "Bengaluru, IN",
    val note: String = "",
    val cashbackWon: Double = 0.0
)
