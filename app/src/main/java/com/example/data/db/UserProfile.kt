package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val age: Int,
    val phone: String,
    val coins: Long = 50L, // Welcome bonus
    val totalCoinsEarned: Long = 50L,
    val adsWatched: Int = 0,
    val streakDays: Int = 1,
    val lastEarnDate: Long = System.currentTimeMillis(),
    val registeredAt: Long = System.currentTimeMillis()
)
