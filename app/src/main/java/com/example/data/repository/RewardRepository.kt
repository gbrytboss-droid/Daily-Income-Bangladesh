package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.db.RewardTransaction
import com.example.data.db.UserProfile
import kotlinx.coroutines.flow.Flow

class RewardRepository(private val db: AppDatabase) {
    val userProfile: Flow<UserProfile?> = db.userDao().getUserProfile()
    val transactions: Flow<List<RewardTransaction>> = db.transactionDao().getAllTransactions()

    suspend fun ensureDefaultUser(): UserProfile {
        val existing = db.userDao().getUserProfileSync()
        if (existing != null) return existing
        val defaultCoins = 50000L // 50,000 Coins = 500 Taka for instant testing
        val profile = UserProfile(
            id = 1,
            name = "Md. Shakil Ahmed",
            age = 24,
            phone = "01712345678",
            coins = defaultCoins,
            totalCoinsEarned = defaultCoins,
            adsWatched = 10,
            streakDays = 3,
            lastEarnDate = System.currentTimeMillis(),
            registeredAt = System.currentTimeMillis()
        )
        db.userDao().insertOrUpdate(profile)
        db.transactionDao().insertTransaction(
            RewardTransaction(
                title = "Welcome Starter Balance",
                adType = "welcome_bonus",
                baseCoins = 100000,
                userSharePercent = 50,
                coinsEarned = 50000
            )
        )
        return profile
    }

    suspend fun addTestCoins(coins: Long = 50000L) {
        val timestamp = System.currentTimeMillis()
        db.userDao().recordAdReward(coins, timestamp)
        db.transactionDao().insertTransaction(
            RewardTransaction(
                title = "Balance Recharge (+৳${coins / 100})",
                adType = "test_recharge",
                baseCoins = (coins * 2).toInt(),
                userSharePercent = 50,
                coinsEarned = coins.toInt()
            )
        )
    }

    suspend fun registerUser(name: String, age: Int, phone: String): UserProfile {
        val welcomeCoins = 50L
        val profile = UserProfile(
            id = 1,
            name = name.trim(),
            age = age,
            phone = phone.trim(),
            coins = welcomeCoins,
            totalCoinsEarned = welcomeCoins,
            adsWatched = 0,
            streakDays = 1,
            lastEarnDate = System.currentTimeMillis(),
            registeredAt = System.currentTimeMillis()
        )
        db.userDao().insertOrUpdate(profile)
        db.transactionDao().insertTransaction(
            RewardTransaction(
                title = "Welcome Registration Bonus",
                adType = "welcome_bonus",
                baseCoins = 100,
                userSharePercent = 50,
                coinsEarned = 50
            )
        )
        return profile
    }

    suspend fun updateProfile(name: String, age: Int, phone: String) {
        db.userDao().updateProfileDetails(name.trim(), age, phone.trim())
    }

    suspend fun claimAdReward(
        title: String,
        adType: String,
        baseCoins: Int,
        userSharePercent: Int = 50
    ): Int {
        val userCoins = (baseCoins * userSharePercent) / 100
        val now = System.currentTimeMillis()
        db.userDao().recordAdReward(userCoins.toLong(), now)
        db.transactionDao().insertTransaction(
            RewardTransaction(
                title = title,
                adType = adType,
                baseCoins = baseCoins,
                userSharePercent = userSharePercent,
                coinsEarned = userCoins,
                timestamp = now
            )
        )
        return userCoins
    }

    suspend fun claimDailyCheckIn(streakDay: Int): Int {
        val bonus = 25 + (streakDay * 5)
        val now = System.currentTimeMillis()
        db.userDao().recordStreakCheckIn(bonus.toLong(), now)
        db.transactionDao().insertTransaction(
            RewardTransaction(
                title = "Day $streakDay Check-In Streak",
                adType = "daily_streak",
                baseCoins = bonus * 2,
                userSharePercent = 50,
                coinsEarned = bonus,
                timestamp = now
            )
        )
        return bonus
    }

    suspend fun requestWithdrawal(
        takaAmount: Int,
        method: String,
        accountNumber: String
    ): Boolean {
        // 1 Taka = 100 coins. Minimum withdrawal 500 Taka = 50,000 coins.
        val coinsRequired = takaAmount.toLong() * 100L
        val rowsUpdated = db.userDao().deductCoinsForWithdrawal(coinsRequired)
        if (rowsUpdated > 0) {
            val now = System.currentTimeMillis()
            db.transactionDao().insertTransaction(
                RewardTransaction(
                    title = "Withdrawal Payout ($method: $accountNumber)",
                    adType = "withdrawal",
                    baseCoins = -(coinsRequired.toInt()),
                    userSharePercent = 100,
                    coinsEarned = -(coinsRequired.toInt()),
                    timestamp = now
                )
            )
            return true
        }
        return false
    }
}
