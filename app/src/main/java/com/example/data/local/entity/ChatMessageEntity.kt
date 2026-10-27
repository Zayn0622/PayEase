package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val contactId: String,
    val sender: String, // "ME", "CONTACT"
    val messageType: String, // "TEXT", "PAYMENT_SENT", "PAYMENT_RECEIVED", "PAYMENT_REQUEST", "IMAGE_ATTACHMENT"
    val text: String = "",
    val amount: Double? = null,
    val transactionId: String? = null,
    val status: String = "SUCCESS", // "SUCCESS", "PENDING", "DECLINED"
    val attachmentUri: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
