package com.example.ui

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.admob.AdMobConfig
import com.example.data.admob.AdMobManager
import com.example.data.db.AppDatabase
import com.example.data.db.RewardTransaction
import com.example.data.db.UserProfile
import com.example.data.repository.RewardRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenTab {
    EARN, WALLET, WITHDRAW, PROFILE
}

data class ActiveAdSimulation(
    val title: String,
    val adType: String,
    val baseCoins: Int,
    val userSharePercent: Int = 50,
    val durationSeconds: Int = 12
)

data class RewardCelebration(
    val coinsAwarded: Int,
    val baseCoins: Int,
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val repository = RewardRepository(database)
    val admobManager = AdMobManager(application)

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val transactions: StateFlow<List<RewardTransaction>> = repository.transactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentTab = MutableStateFlow(ScreenTab.EARN)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    private val _activeSimulation = MutableStateFlow<ActiveAdSimulation?>(null)
    val activeSimulation: StateFlow<ActiveAdSimulation?> = _activeSimulation.asStateFlow()

    private val _celebration = MutableSharedFlow<RewardCelebration>()
    val celebration: SharedFlow<RewardCelebration> = _celebration.asSharedFlow()

    private val _showConfigDialog = MutableStateFlow(false)
    val showConfigDialog: StateFlow<Boolean> = _showConfigDialog.asStateFlow()

    private val _showEditProfileDialog = MutableStateFlow(false)
    val showEditProfileDialog: StateFlow<Boolean> = _showEditProfileDialog.asStateFlow()

    private val _showWithdrawDialog = MutableStateFlow(false)
    val showWithdrawDialog: StateFlow<Boolean> = _showWithdrawDialog.asStateFlow()

    private val _withdrawFeedback = MutableStateFlow<String?>(null)
    val withdrawFeedback: StateFlow<String?> = _withdrawFeedback.asStateFlow()

    private val _fcmToken = MutableStateFlow<String?>(null)
    val fcmToken: StateFlow<String?> = _fcmToken.asStateFlow()

    val admobConfig: StateFlow<AdMobConfig> = admobManager.config
    val isAdLoaded: StateFlow<Boolean> = admobManager.isAdLoaded
    val adStatusMessage: StateFlow<String> = admobManager.statusMessage

    init {
        viewModelScope.launch {
            repository.ensureDefaultUser()
        }
    }

    fun addTestBalance() {
        viewModelScope.launch {
            repository.addTestCoins(50000L)
            _celebration.emit(
                RewardCelebration(
                    coinsAwarded = 50000,
                    baseCoins = 100000,
                    title = "Test Balance Recharge (+৳500)"
                )
            )
        }
    }

    fun setTab(tab: ScreenTab) {
        _currentTab.value = tab
    }

    fun setShowConfigDialog(show: Boolean) {
        _showConfigDialog.value = show
    }

    fun setShowEditProfileDialog(show: Boolean) {
        _showEditProfileDialog.value = show
    }

    fun setShowWithdrawDialog(show: Boolean) {
        _showWithdrawDialog.value = show
        _withdrawFeedback.value = null
    }

    fun clearWithdrawFeedback() {
        _withdrawFeedback.value = null
    }

    fun submitWithdrawal(amountTaka: Int, method: String, accountNumber: String) {
        viewModelScope.launch {
            if (amountTaka < 500) {
                _withdrawFeedback.value = "মিনিমাম উইথড্রয়াল ৫০০ টাকা!"
                return@launch
            }
            val success = repository.requestWithdrawal(amountTaka, method, accountNumber)
            if (success) {
                _withdrawFeedback.value = "সফল হয়েছে! ৳$amountTaka টাকা $method নম্বরে প্রসেসিং শুরু হয়েছে।"
            } else {
                _withdrawFeedback.value = "পর্যাপ্ত ব্যালেন্স নেই! ৫০০ টাকার জন্য ৫০,০০০ কয়েন প্রয়োজন।"
            }
        }
    }

    fun registerUser(name: String, age: Int, phone: String) {
        viewModelScope.launch {
            repository.registerUser(name, age, phone)
            _celebration.emit(
                RewardCelebration(
                    coinsAwarded = 50,
                    baseCoins = 100,
                    title = "Welcome Bonus"
                )
            )
        }
    }

    fun updateProfile(name: String, age: Int, phone: String) {
        viewModelScope.launch {
            repository.updateProfile(name, age, phone)
            _showEditProfileDialog.value = false
        }
    }

    fun watchRewardedAd(
        activity: Activity,
        title: String = "AdMob Rewarded Video",
        adType: String = "admob_rewarded",
        baseCoins: Int = 100,
        userSharePercent: Int = 50
    ) {
        admobManager.showAd(
            activity = activity,
            onRewardEarned = {
                awardCoins(title, adType, baseCoins, userSharePercent)
            },
            onFallbackSimulation = {
                // Trigger full interactive animated ad simulator
                _activeSimulation.value = ActiveAdSimulation(
                    title = title,
                    adType = adType,
                    baseCoins = baseCoins,
                    userSharePercent = userSharePercent,
                    durationSeconds = 12
                )
            }
        )
    }

    fun completeSimulationAd() {
        val sim = _activeSimulation.value ?: return
        _activeSimulation.value = null
        awardCoins(sim.title, sim.adType, sim.baseCoins, sim.userSharePercent)
    }

    fun dismissSimulationAd() {
        _activeSimulation.value = null
    }

    private fun awardCoins(
        title: String,
        adType: String,
        baseCoins: Int,
        userSharePercent: Int
    ) {
        viewModelScope.launch {
            val awarded = repository.claimAdReward(title, adType, baseCoins, userSharePercent)
            _celebration.emit(
                RewardCelebration(
                    coinsAwarded = awarded,
                    baseCoins = baseCoins,
                    title = title
                )
            )
        }
    }

    fun claimDailyStreak() {
        val currentStreak = userProfile.value?.streakDays ?: 1
        viewModelScope.launch {
            val awarded = repository.claimDailyCheckIn(currentStreak)
            _celebration.emit(
                RewardCelebration(
                    coinsAwarded = awarded,
                    baseCoins = awarded * 2,
                    title = "Daily Streak Check-In"
                )
            )
        }
    }

    fun saveAdMobConfig(appId: String, unitId: String) {
        admobManager.updateConfig(appId, unitId)
        _showConfigDialog.value = false
    }

    fun resetAdMobConfig() {
        admobManager.resetToTestKey()
        _showConfigDialog.value = false
    }

    fun updateFcmToken(token: String) {
        _fcmToken.value = token
    }

    fun sendTestReminderNotification() {
        val app = getApplication<Application>()
        val profile = userProfile.value
        val coins = profile?.coins ?: 0L
        val currentTaka = String.format("%.2f", coins * 0.01)
        val remainingTaka = String.format("%.2f", (500.0 - (coins * 0.01)).coerceAtLeast(0.0))

        com.example.notification.NotificationHelper.showEarningReminderNotification(
            context = app,
            title = "আজকের ৳৫০০ টাকার লক্ষ্য পূরণ করুন! 🪙",
            body = "আপনার বর্তমান ব্যালেন্স: ৳$currentTaka টাকা। ৫০০ টাকার উইথড্র লক্ষ্যের জন্য বাকি মাত্র ৳$remainingTaka টাকা! প্রতি Ad এ পান ০.৫০ টাকা।"
        )
    }
}
