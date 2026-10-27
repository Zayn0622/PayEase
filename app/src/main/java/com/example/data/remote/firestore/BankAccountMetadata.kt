package com.example.data.remote.firestore

import com.example.data.local.entity.BankAccountEntity

data class BankAccountMetadata(
    val id: String = "",
    val bankName: String = "",
    val bankCode: String = "",
    val accountNumberMasked: String = "",
    val accountType: String = "Savings Account",
    val ifscCode: String = "",
    val balance: Double = 0.0,
    val isPrimary: Boolean = false,
    val cardGradientStart: Long = 0xFF0D47A1,
    val cardGradientEnd: Long = 0xFF1976D2,
    val linkedAt: Long = System.currentTimeMillis(),
    val upiPin: String = "1234"
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "bankName" to bankName,
        "bankCode" to bankCode,
        "accountNumberMasked" to accountNumberMasked,
        "accountType" to accountType,
        "ifscCode" to ifscCode,
        "balance" to balance,
        "isPrimary" to isPrimary,
        "cardGradientStart" to cardGradientStart,
        "cardGradientEnd" to cardGradientEnd,
        "linkedAt" to linkedAt,
        "upiPin" to upiPin
    )

    fun toEntity(): BankAccountEntity = BankAccountEntity(
        id = id,
        bankName = bankName,
        bankCode = bankCode,
        accountNumberMasked = accountNumberMasked,
        accountType = accountType,
        ifscCode = ifscCode,
        balance = balance,
        isPrimary = isPrimary,
        upiPin = upiPin,
        cardGradientStart = cardGradientStart,
        cardGradientEnd = cardGradientEnd
    )

    companion object {
        fun fromEntity(entity: BankAccountEntity, linkedAt: Long = System.currentTimeMillis()): BankAccountMetadata =
            BankAccountMetadata(
                id = entity.id,
                bankName = entity.bankName,
                bankCode = entity.bankCode,
                accountNumberMasked = entity.accountNumberMasked,
                accountType = entity.accountType,
                ifscCode = entity.ifscCode,
                balance = entity.balance,
                isPrimary = entity.isPrimary,
                cardGradientStart = entity.cardGradientStart,
                cardGradientEnd = entity.cardGradientEnd,
                linkedAt = linkedAt,
                upiPin = entity.upiPin
            )

        fun fromMap(data: Map<String, Any?>): BankAccountMetadata =
            BankAccountMetadata(
                id = data["id"] as? String ?: "",
                bankName = data["bankName"] as? String ?: "",
                bankCode = data["bankCode"] as? String ?: "",
                accountNumberMasked = data["accountNumberMasked"] as? String ?: "",
                accountType = data["accountType"] as? String ?: "Savings Account",
                ifscCode = data["ifscCode"] as? String ?: "",
                balance = (data["balance"] as? Number)?.toDouble() ?: 0.0,
                isPrimary = data["isPrimary"] as? Boolean ?: false,
                cardGradientStart = (data["cardGradientStart"] as? Number)?.toLong() ?: 0xFF0D47A1,
                cardGradientEnd = (data["cardGradientEnd"] as? Number)?.toLong() ?: 0xFF1976D2,
                linkedAt = (data["linkedAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                upiPin = data["upiPin"] as? String ?: "1234"
            )
    }
}
