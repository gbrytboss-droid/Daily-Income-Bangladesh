package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reward_transactions")
data class RewardTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val adType: String,
    val baseCoins: Int,
    val userSharePercent: Int = 50,
    val coinsEarned: Int,
    val timestamp: Long = System.currentTimeMillis()
)
