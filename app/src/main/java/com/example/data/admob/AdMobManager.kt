package com.example.data.admob

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AdMobConfig(
    val appId: String = "ca-app-pub-3940256099942544~3347511713",
    val rewardedUnitId: String = "ca-app-pub-3940256099942544/5224354917",
    val isCustomKeyApplied: Boolean = false,
    val simulationFallbackEnabled: Boolean = true
)

class AdMobManager(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("admob_reward_prefs", Context.MODE_PRIVATE)

    private val _config = MutableStateFlow(loadConfig())
    val config: StateFlow<AdMobConfig> = _config.asStateFlow()

    private val _isAdLoaded = MutableStateFlow(false)
    val isAdLoaded: StateFlow<Boolean> = _isAdLoaded.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _statusMessage = MutableStateFlow("AdMob SDK Ready (Test Mode)")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    private var rewardedAd: RewardedAd? = null

    init {
        try {
            MobileAds.initialize(context) { initializationStatus ->
                Log.d("AdMobManager", "AdMob initialized: $initializationStatus")
                _statusMessage.value = "AdMob Initialized"
                loadRewardedAd()
            }
        } catch (e: Exception) {
            Log.e("AdMobManager", "Error initializing AdMob", e)
            _statusMessage.value = "AdMob ready with Interactive Simulator"
        }
    }

    private fun loadConfig(): AdMobConfig {
        val savedAppId = prefs.getString("admob_app_id", "ca-app-pub-3940256099942544~3347511713")
            ?: "ca-app-pub-3940256099942544~3347511713"
        val savedUnitId = prefs.getString("admob_rewarded_unit_id", "ca-app-pub-3940256099942544/5224354917")
            ?: "ca-app-pub-3940256099942544/5224354917"
        val isCustom = prefs.getBoolean("admob_is_custom", false)
        return AdMobConfig(
            appId = savedAppId,
            rewardedUnitId = savedUnitId,
            isCustomKeyApplied = isCustom
        )
    }

    fun updateConfig(appId: String, unitId: String) {
        val cleanAppId = appId.trim().ifEmpty { "ca-app-pub-3940256099942544~3347511713" }
        val cleanUnitId = unitId.trim().ifEmpty { "ca-app-pub-3940256099942544/5224354917" }
        val isCustom = cleanUnitId != "ca-app-pub-3940256099942544/5224354917"

        prefs.edit()
            .putString("admob_app_id", cleanAppId)
            .putString("admob_rewarded_unit_id", cleanUnitId)
            .putBoolean("admob_is_custom", isCustom)
            .apply()

        _config.value = AdMobConfig(
            appId = cleanAppId,
            rewardedUnitId = cleanUnitId,
            isCustomKeyApplied = isCustom
        )
        _statusMessage.value = if (isCustom) "Custom AdMob Key Applied" else "Sample Test Ad Key Active"
        loadRewardedAd()
    }

    fun resetToTestKey() {
        updateConfig(
            "ca-app-pub-3940256099942544~3347511713",
            "ca-app-pub-3940256099942544/5224354917"
        )
    }

    fun loadRewardedAd() {
        if (_isLoading.value) return
        _isLoading.value = true
        _statusMessage.value = "Requesting Rewarded Ad..."

        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            _config.value.rewardedUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    _isAdLoaded.value = true
                    _isLoading.value = false
                    _statusMessage.value = "Ad Loaded & Ready"
                    Log.d("AdMobManager", "AdMob Rewarded Ad loaded successfully.")

                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            rewardedAd = null
                            _isAdLoaded.value = false
                            loadRewardedAd()
                        }

                        override fun onAdFailedToShowFullScreenContent(error: AdError) {
                            Log.e("AdMobManager", "Ad failed to show: ${error.message}")
                            rewardedAd = null
                            _isAdLoaded.value = false
                            _statusMessage.value = "Ad failed to display: ${error.message}"
                            loadRewardedAd()
                        }

                        override fun onAdShowedFullScreenContent() {
                            _statusMessage.value = "Watching Rewarded Ad..."
                        }
                    }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.w("AdMobManager", "Ad failed to load: ${loadAdError.message}")
                    rewardedAd = null
                    _isAdLoaded.value = false
                    _isLoading.value = false
                    _statusMessage.value = "Ad request completed (Ready for preview)"
                }
            }
        )
    }

    fun showAd(
        activity: Activity,
        onRewardEarned: () -> Unit,
        onFallbackSimulation: () -> Unit
    ) {
        val currentAd = rewardedAd
        if (currentAd != null) {
            currentAd.show(activity) { rewardItem ->
                Log.d("AdMobManager", "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
                onRewardEarned()
            }
        } else {
            // If ad is not loaded or device has no Google Play Services / Ad inventory, trigger simulation
            onFallbackSimulation()
        }
    }
}
