package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rewards")
data class RewardEntity(
    @PrimaryKey val id: String,
    val transactionId: String,
    val amount: Double,
    val isScratched: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val title: String = "Guaranteed Cashback",
    val description: String = "Credited to your primary bank account"
)
