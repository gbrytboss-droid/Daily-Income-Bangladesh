package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM reward_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<RewardTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: RewardTransaction)

    @Query("SELECT COUNT(*) FROM reward_transactions")
    suspend fun getTransactionCount(): Int

    @Query("DELETE FROM reward_transactions")
    suspend fun clearAll()
}
