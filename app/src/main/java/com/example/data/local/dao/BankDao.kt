package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.BankAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BankDao {
    @Query("SELECT * FROM bank_accounts ORDER BY isPrimary DESC, bankName ASC")
    fun getAllBankAccounts(): Flow<List<BankAccountEntity>>

    @Query("SELECT * FROM bank_accounts WHERE id = :id LIMIT 1")
    suspend fun getAccountById(id: String): BankAccountEntity?

    @Query("SELECT * FROM bank_accounts WHERE isPrimary = 1 LIMIT 1")
    suspend fun getPrimaryAccount(): BankAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: BankAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(accounts: List<BankAccountEntity>)

    @Update
    suspend fun updateAccount(account: BankAccountEntity)

    @Query("UPDATE bank_accounts SET isPrimary = 0")
    suspend fun clearPrimaryFlag()

    @Query("UPDATE bank_accounts SET isPrimary = 1 WHERE id = :id")
    suspend fun setPrimaryAccount(id: String)

    @Query("UPDATE bank_accounts SET balance = :newBalance WHERE id = :id")
    suspend fun updateBalance(id: String, newBalance: Double)

    @Query("DELETE FROM bank_accounts WHERE id = :id")
    suspend fun deleteAccount(id: String)
}
