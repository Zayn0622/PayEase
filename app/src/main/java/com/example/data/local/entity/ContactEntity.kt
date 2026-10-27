package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey val id: String,
    val name: String,
    val vpa: String,
    val phone: String,
    val avatarColor: Long = 0xFF1E88E5,
    val lastMessage: String = "",
    val lastMessageTime: Long = System.currentTimeMillis()
)
