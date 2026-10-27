package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bank_accounts")
data class BankAccountEntity(
    @PrimaryKey val id: String,
    val bankName: String,
    val bankCode: String, // e.g. "HDFC", "SBI", "ICICI", "AXIS"
    val accountNumberMasked: String, // e.g. "•••• 4821"
    val accountType: String, // "Savings", "Current"
    val ifscCode: String,
    val balance: Double,
    val isPrimary: Boolean,
    val upiPin: String = "1234",
    val cardGradientStart: Long = 0xFF0D47A1,
    val cardGradientEnd: Long = 0xFF1976D2
)
