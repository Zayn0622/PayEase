package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.RewardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RewardDao {
    @Query("SELECT * FROM rewards ORDER BY timestamp DESC")
    fun getAllRewards(): Flow<List<RewardEntity>>

    @Query("SELECT SUM(amount) FROM rewards WHERE isScratched = 1")
    fun getTotalCashbackWon(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM rewards WHERE isScratched = 0")
    fun getUnclaimedCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReward(reward: RewardEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rewards: List<RewardEntity>)

    @Query("UPDATE rewards SET isScratched = 1 WHERE id = :id")
    suspend fun markScratched(id: String)
}
