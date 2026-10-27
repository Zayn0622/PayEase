package com.example.data.remote.firestore

import com.example.data.local.entity.TransactionEntity

data class TransactionFirestoreModel(
    val id: String = "",
    val contactId: String? = null,
    val contactName: String = "",
    val contactVpa: String = "",
    val amount: Double = 0.0,
    val type: String = "SENT",
    val status: String = "SUCCESS",
    val timestamp: Long = System.currentTimeMillis(),
    val upiRefId: String = "",
    val bankAccountId: String = "",
    val bankName: String = "",
    val locationTag: String = "Bengaluru, IN",
    val note: String = "",
    val cashbackWon: Double = 0.0
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "contactId" to contactId,
        "contactName" to contactName,
        "contactVpa" to contactVpa,
        "amount" to amount,
        "type" to type,
        "status" to status,
        "timestamp" to timestamp,
        "upiRefId" to upiRefId,
        "bankAccountId" to bankAccountId,
        "bankName" to bankName,
        "locationTag" to locationTag,
        "note" to note,
        "cashbackWon" to cashbackWon
    )

    fun toEntity(): TransactionEntity = TransactionEntity(
        id = id,
        contactId = contactId,
        contactName = contactName,
        contactVpa = contactVpa,
        amount = amount,
        type = type,
        status = status,
        timestamp = timestamp,
        upiRefId = upiRefId,
        bankAccountId = bankAccountId,
        bankName = bankName,
        locationTag = locationTag,
        note = note,
        cashbackWon = cashbackWon
    )

    companion object {
        fun fromEntity(entity: TransactionEntity): TransactionFirestoreModel =
            TransactionFirestoreModel(
                id = entity.id,
                contactId = entity.contactId,
                contactName = entity.contactName,
                contactVpa = entity.contactVpa,
                amount = entity.amount,
                type = entity.type,
                status = entity.status,
                timestamp = entity.timestamp,
                upiRefId = entity.upiRefId,
                bankAccountId = entity.bankAccountId,
                bankName = entity.bankName,
                locationTag = entity.locationTag,
                note = entity.note,
                cashbackWon = entity.cashbackWon
            )

        fun fromMap(data: Map<String, Any?>): TransactionFirestoreModel =
            TransactionFirestoreModel(
                id = data["id"] as? String ?: "",
                contactId = data["contactId"] as? String,
                contactName = data["contactName"] as? String ?: "",
                contactVpa = data["contactVpa"] as? String ?: "",
                amount = (data["amount"] as? Number)?.toDouble() ?: 0.0,
                type = data["type"] as? String ?: "SENT",
                status = data["status"] as? String ?: "SUCCESS",
                timestamp = (data["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                upiRefId = data["upiRefId"] as? String ?: "",
                bankAccountId = data["bankAccountId"] as? String ?: "",
                bankName = data["bankName"] as? String ?: "",
                locationTag = data["locationTag"] as? String ?: "Bengaluru, IN",
                note = data["note"] as? String ?: "",
                cashbackWon = (data["cashbackWon"] as? Number)?.toDouble() ?: 0.0
            )
    }
}
