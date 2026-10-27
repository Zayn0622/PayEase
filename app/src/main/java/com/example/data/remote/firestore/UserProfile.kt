package com.example.data.remote.firestore

data class UserProfile(
    val userId: String = "user_default",
    val displayName: String = "Alex Morgan",
    val vpa: String = "payease.user@payease",
    val phoneNumber: String = "+91 98765 43210",
    val email: String = "alex.morgan@payease.app",
    val upiNumber: String = "9876543210",
    val avatarInitials: String = "AM",
    val avatarType: String = "PRESET_3D_MALE", // PRESET_3D_MALE, PRESET_3D_FEMALE, PRESET_EMOJI, INITIALS
    val avatarEmoji: String = "🚀",
    val avatarBgColor: Long = 0xFF2563EB,
    val kycStatus: String = "VERIFIED",
    val dailyLimit: Double = 25000.0,
    val isDailyLimitEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "displayName" to displayName,
        "vpa" to vpa,
        "phoneNumber" to phoneNumber,
        "email" to email,
        "upiNumber" to upiNumber,
        "avatarInitials" to avatarInitials,
        "avatarType" to avatarType,
        "avatarEmoji" to avatarEmoji,
        "avatarBgColor" to avatarBgColor,
        "kycStatus" to kycStatus,
        "dailyLimit" to dailyLimit,
        "isDailyLimitEnabled" to isDailyLimitEnabled,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )

    companion object {
        fun fromMap(data: Map<String, Any?>): UserProfile {
            return UserProfile(
                userId = data["userId"] as? String ?: "user_default",
                displayName = data["displayName"] as? String ?: "Alex Morgan",
                vpa = data["vpa"] as? String ?: "payease.user@payease",
                phoneNumber = data["phoneNumber"] as? String ?: "+91 98765 43210",
                email = data["email"] as? String ?: "alex.morgan@payease.app",
                upiNumber = data["upiNumber"] as? String ?: "9876543210",
                avatarInitials = data["avatarInitials"] as? String ?: "AM",
                avatarType = data["avatarType"] as? String ?: "PRESET_3D_MALE",
                avatarEmoji = data["avatarEmoji"] as? String ?: "🚀",
                avatarBgColor = (data["avatarBgColor"] as? Number)?.toLong() ?: 0xFF2563EB,
                kycStatus = data["kycStatus"] as? String ?: "VERIFIED",
                dailyLimit = (data["dailyLimit"] as? Number)?.toDouble() ?: 25000.0,
                isDailyLimitEnabled = data["isDailyLimitEnabled"] as? Boolean ?: true,
                createdAt = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }
    }
}
