package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.BankDao
import com.example.data.local.dao.ChatDao
import com.example.data.local.dao.RewardDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.entity.BankAccountEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ContactEntity
import com.example.data.local.entity.RewardEntity
import com.example.data.local.entity.TransactionEntity

@Database(
    entities = [
        BankAccountEntity::class,
        TransactionEntity::class,
        ContactEntity::class,
        ChatMessageEntity::class,
        RewardEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bankDao(): BankDao
    abstract fun transactionDao(): TransactionDao
    abstract fun chatDao(): ChatDao
    abstract fun rewardDao(): RewardDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "payease_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
