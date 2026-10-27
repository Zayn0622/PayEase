package com.example.data.remote.firestore

import android.content.Context
import android.util.Log
import com.example.data.local.entity.BankAccountEntity
import com.example.data.local.entity.TransactionEntity
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext

class FirestoreSyncService(context: Context) {
    private val TAG = "FirestoreSyncService"
    private val firestore = FirestoreManager.getFirestore(context)

    // Collection References
    private val usersCollection = firestore.collection("users")
    private val transactionsCollection = firestore.collection("transactions")
    private val bankAccountsCollection = firestore.collection("bank_accounts")

    // ==========================================
    // 1. USER PROFILE PERSISTENT STORAGE
    // ==========================================

    suspend fun saveUserProfile(profile: UserProfile): Boolean = withContext(Dispatchers.IO) {
        try {
            usersCollection.document(profile.userId)
                .set(profile.toMap(), SetOptions.merge())
                .awaitTask()
            Log.d(TAG, "User profile persisted to Firestore for ${profile.displayName} (${profile.userId})")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save user profile to Firestore", e)
            false
        }
    }

    suspend fun getUserProfile(userId: String = "user_default"): UserProfile? = withContext(Dispatchers.IO) {
        try {
            val snapshot = usersCollection.document(userId).get().awaitTask()
            if (snapshot.exists()) {
                val data = snapshot.data ?: return@withContext null
                UserProfile.fromMap(data)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch user profile from Firestore", e)
            null
        }
    }

    fun observeUserProfile(userId: String = "user_default"): Flow<UserProfile?> = callbackFlow {
        val listener = usersCollection.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening to user profile changes", error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    snapshot.data?.let {
                        trySend(UserProfile.fromMap(it))
                    }
                } else {
                    trySend(null)
                }
            }
        awaitClose { listener.remove() }
    }

    // ==========================================
    // 2. TRANSACTION HISTORY PERSISTENT STORAGE
    // ==========================================

    suspend fun saveTransaction(transaction: TransactionEntity): Boolean = withContext(Dispatchers.IO) {
        try {
            val model = TransactionFirestoreModel.fromEntity(transaction)
            transactionsCollection.document(transaction.id)
                .set(model.toMap(), SetOptions.merge())
                .awaitTask()
            Log.d(TAG, "Transaction ${transaction.id} persisted to Firestore")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save transaction ${transaction.id} to Firestore", e)
            false
        }
    }

    suspend fun saveTransactionsBatch(transactions: List<TransactionEntity>): Boolean = withContext(Dispatchers.IO) {
        if (transactions.isEmpty()) return@withContext true
        try {
            val batch = firestore.batch()
            for (tx in transactions) {
                val docRef = transactionsCollection.document(tx.id)
                batch.set(docRef, TransactionFirestoreModel.fromEntity(tx).toMap(), SetOptions.merge())
            }
            batch.commit().awaitTask()
            Log.d(TAG, "Batch of ${transactions.size} transactions persisted to Firestore")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to batch save transactions to Firestore", e)
            false
        }
    }

    suspend fun fetchTransactions(): List<TransactionEntity> = withContext(Dispatchers.IO) {
        try {
            val snapshot = transactionsCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .awaitTask()
            snapshot.documents.mapNotNull { doc ->
                doc.data?.let { TransactionFirestoreModel.fromMap(it).toEntity() }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch transactions from Firestore", e)
            emptyList()
        }
    }

    fun observeTransactions(): Flow<List<TransactionEntity>> = callbackFlow {
        val listener = transactionsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error observing transactions from Firestore", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.data?.let { TransactionFirestoreModel.fromMap(it).toEntity() }
                    }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    // ==========================================
    // 3. LINKED BANK ACCOUNT METADATA
    // ==========================================

    suspend fun saveBankAccountMetadata(bank: BankAccountEntity): Boolean = withContext(Dispatchers.IO) {
        try {
            val metadata = BankAccountMetadata.fromEntity(bank)
            bankAccountsCollection.document(bank.id)
                .set(metadata.toMap(), SetOptions.merge())
                .awaitTask()
            Log.d(TAG, "Bank metadata ${bank.bankName} (${bank.id}) persisted to Firestore")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save bank account metadata to Firestore", e)
            false
        }
    }

    suspend fun saveBankAccountsBatch(banks: List<BankAccountEntity>): Boolean = withContext(Dispatchers.IO) {
        if (banks.isEmpty()) return@withContext true
        try {
            val batch = firestore.batch()
            for (bank in banks) {
                val docRef = bankAccountsCollection.document(bank.id)
                batch.set(docRef, BankAccountMetadata.fromEntity(bank).toMap(), SetOptions.merge())
            }
            batch.commit().awaitTask()
            Log.d(TAG, "Batch of ${banks.size} bank accounts persisted to Firestore")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to batch save bank metadata to Firestore", e)
            false
        }
    }

    suspend fun updateBankAccountBalance(bankId: String, newBalance: Double): Boolean = withContext(Dispatchers.IO) {
        try {
            bankAccountsCollection.document(bankId)
                .update("balance", newBalance)
                .awaitTask()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update bank balance in Firestore", e)
            false
        }
    }

    suspend fun setPrimaryBankAccount(bankId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val snapshot = bankAccountsCollection.get().awaitTask()
            val batch = firestore.batch()
            for (doc in snapshot.documents) {
                val isCurrent = doc.id == bankId
                batch.update(doc.reference, "isPrimary", isCurrent)
            }
            batch.commit().awaitTask()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update primary bank in Firestore", e)
            false
        }
    }

    suspend fun fetchBankAccountMetadata(): List<BankAccountEntity> = withContext(Dispatchers.IO) {
        try {
            val snapshot = bankAccountsCollection.get().awaitTask()
            snapshot.documents.mapNotNull { doc ->
                doc.data?.let { BankAccountMetadata.fromMap(it).toEntity() }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch bank accounts from Firestore", e)
            emptyList()
        }
    }

    fun observeBankAccountMetadata(): Flow<List<BankAccountEntity>> = callbackFlow {
        val listener = bankAccountsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error observing bank accounts from Firestore", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.data?.let { BankAccountMetadata.fromMap(it).toEntity() }
                    }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }
}
