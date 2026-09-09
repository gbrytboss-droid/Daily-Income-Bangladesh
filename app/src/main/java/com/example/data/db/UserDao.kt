package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileSync(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfile)

    @Query("UPDATE user_profile SET name = :name, age = :age, phone = :phone WHERE id = 1")
    suspend fun updateProfileDetails(name: String, age: Int, phone: String)

    @Query("""
        UPDATE user_profile 
        SET coins = coins + :coinsEarned,
            totalCoinsEarned = totalCoinsEarned + :coinsEarned,
            adsWatched = adsWatched + 1,
            lastEarnDate = :timestamp
        WHERE id = 1
    """)
    suspend fun recordAdReward(coinsEarned: Long, timestamp: Long)

    @Query("""
        UPDATE user_profile 
        SET streakDays = streakDays + 1,
            coins = coins + :bonusCoins,
            totalCoinsEarned = totalCoinsEarned + :bonusCoins,
            lastEarnDate = :timestamp
        WHERE id = 1
    """)
    suspend fun recordStreakCheckIn(bonusCoins: Long, timestamp: Long)

    @Query("UPDATE user_profile SET coins = coins - :coinsToDeduct WHERE id = 1 AND coins >= :coinsToDeduct")
    suspend fun deductCoinsForWithdrawal(coinsToDeduct: Long): Int
}
