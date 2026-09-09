package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notification.NotificationHelper
import com.example.ui.MainViewModel
import com.example.ui.RewardCelebration
import com.example.ui.ScreenTab
import com.example.ui.components.AdMobConfigDialog
import com.example.ui.components.AdSimulationDialog
import com.example.ui.components.AnimatedCoinBurst
import com.example.ui.components.WithdrawalDialog
import com.example.ui.screens.EarnScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RegisterScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.screens.WithdrawScreen
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CoinRewardApp()
            }
        }
    }
}

@Composable
fun CoinRewardApp(viewModel: MainViewModel = viewModel()) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val activeSimulation by viewModel.activeSimulation.collectAsStateWithLifecycle()
    val showConfigDialog by viewModel.showConfigDialog.collectAsStateWithLifecycle()
    val showEditProfileDialog by viewModel.showEditProfileDialog.collectAsStateWithLifecycle()
    val showWithdrawDialog by viewModel.showWithdrawDialog.collectAsStateWithLifecycle()
    val withdrawFeedback by viewModel.withdrawFeedback.collectAsStateWithLifecycle()
    val fcmToken by viewModel.fcmToken.collectAsStateWithLifecycle()
    val admobConfig by viewModel.admobConfig.collectAsStateWithLifecycle()
    val isAdLoaded by viewModel.isAdLoaded.collectAsStateWithLifecycle()
    val adStatusMessage by viewModel.adStatusMessage.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Permission result handled
    }

    var activeCelebration by remember { mutableStateOf<RewardCelebration?>(null) }

    LaunchedEffect(Unit) {
        try {
            NotificationHelper.initFcm(context) { token ->
                viewModel.updateFcmToken(token)
            }
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Silent FCM init: ${e.message}")
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.celebration.collectLatest { celebration ->
            activeCelebration = celebration
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        if (userProfile == null) {
            // First time registration screen (Name, Age, Phone)
            RegisterScreen(
                onRegister = { name, age, phone ->
                    viewModel.registerUser(name, age, phone)
                },
                onQuickDemo = {
                    viewModel.registerUser("Sabbir Ahmed", 24, "01712345678")
                }
            )
        } else {
            // Main Application Scaffold
            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing,
                containerColor = BackgroundDark,
                bottomBar = {
                    NavigationBar(
                        containerColor = SurfaceDark,
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .navigationBarsPadding()
                            .testTag("bottom_nav_bar")
                    ) {
                        NavigationBarItem(
                            selected = currentTab == ScreenTab.EARN,
                            onClick = { viewModel.setTab(ScreenTab.EARN) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == ScreenTab.EARN) Icons.Filled.MonetizationOn else Icons.Outlined.MonetizationOn,
                                    contentDescription = "Earn"
                                )
                            },
                            label = { Text("Earn", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BackgroundDark,
                                selectedTextColor = GoldPrimary,
                                indicatorColor = GoldPrimary,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary
                            )
                        )

                        NavigationBarItem(
                            selected = currentTab == ScreenTab.WALLET,
                            onClick = { viewModel.setTab(ScreenTab.WALLET) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == ScreenTab.WALLET) Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet,
                                    contentDescription = "Wallet"
                                )
                            },
                            label = { Text("Wallet", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BackgroundDark,
                                selectedTextColor = GoldPrimary,
                                indicatorColor = GoldPrimary,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary
                            )
                        )

                        NavigationBarItem(
                            selected = currentTab == ScreenTab.WITHDRAW,
                            onClick = { viewModel.setTab(ScreenTab.WITHDRAW) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == ScreenTab.WITHDRAW) Icons.Filled.AccountBalance else Icons.Outlined.AccountBalance,
                                    contentDescription = "Withdraw"
                                )
                            },
                            label = { Text("Withdraw", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BackgroundDark,
                                selectedTextColor = GoldPrimary,
                                indicatorColor = GoldPrimary,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary
                            )
                        )

                        NavigationBarItem(
                            selected = currentTab == ScreenTab.PROFILE,
                            onClick = { viewModel.setTab(ScreenTab.PROFILE) },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == ScreenTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                                    contentDescription = "Profile"
                                )
                            },
                            label = { Text("Profile", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BackgroundDark,
                                selectedTextColor = GoldPrimary,
                                indicatorColor = GoldPrimary,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary
                            )
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Crossfade(
                        targetState = currentTab,
                        label = "tab_crossfade"
                    ) { tab ->
                        when (tab) {
                            ScreenTab.EARN -> EarnScreen(
                                profile = userProfile,
                                adMobConfig = admobConfig,
                                isAdLoaded = isAdLoaded,
                                adStatusMessage = adStatusMessage,
                                onWatchAd = { activity, title, adType, baseCoins, sharePercent ->
                                    viewModel.watchRewardedAd(activity, title, adType, baseCoins, sharePercent)
                                },
                                onClaimStreak = { viewModel.claimDailyStreak() },
                                onOpenAdMobConfig = { viewModel.setShowConfigDialog(true) }
                            )

                            ScreenTab.WALLET -> WalletScreen(
                                profile = userProfile,
                                transactions = transactions,
                                onOpenWithdraw = { viewModel.setTab(ScreenTab.WITHDRAW) }
                            )

                            ScreenTab.WITHDRAW -> WithdrawScreen(
                                profile = userProfile,
                                transactions = transactions,
                                feedbackMessage = withdrawFeedback,
                                onClearFeedback = { viewModel.clearWithdrawFeedback() },
                                onSubmitWithdrawal = { amount, method, account ->
                                    viewModel.submitWithdrawal(amount, method, account)
                                },
                                onNavigateToEarn = { viewModel.setTab(ScreenTab.EARN) }
                            )

                            ScreenTab.PROFILE -> ProfileScreen(
                                profile = userProfile,
                                adMobConfig = admobConfig,
                                showEditDialog = showEditProfileDialog,
                                fcmToken = fcmToken,
                                onOpenEditDialog = { viewModel.setShowEditProfileDialog(true) },
                                onCloseEditDialog = { viewModel.setShowEditProfileDialog(false) },
                                onUpdateProfile = { name, age, phone ->
                                    viewModel.updateProfile(name, age, phone)
                                },
                                onOpenAdMobConfig = { viewModel.setShowConfigDialog(true) },
                                onSendTestNotification = { viewModel.sendTestReminderNotification() }
                            )
                        }
                    }
                }
            }
        }

        // Ad Simulation Dialog
        activeSimulation?.let { sim ->
            AdSimulationDialog(
                simulation = sim,
                onComplete = { viewModel.completeSimulationAd() },
                onDismiss = { viewModel.dismissSimulationAd() }
            )
        }

        // AdMob Key Config Dialog
        if (showConfigDialog) {
            AdMobConfigDialog(
                config = admobConfig,
                onSave = { appId, unitId ->
                    viewModel.saveAdMobConfig(appId, unitId)
                },
                onReset = { viewModel.resetAdMobConfig() },
                onDismiss = { viewModel.setShowConfigDialog(false) }
            )
        }

        // Withdrawal Dialog (Minimum 500 Taka)
        if (showWithdrawDialog) {
            WithdrawalDialog(
                profile = userProfile,
                feedbackMessage = withdrawFeedback,
                onSubmit = { amountTaka, method, account ->
                    viewModel.submitWithdrawal(amountTaka, method, account)
                },
                onDismiss = { viewModel.setShowWithdrawDialog(false) }
            )
        }

        // Celebratory Coin Particle Burst
        AnimatedCoinBurst(
            celebration = activeCelebration,
            onDismiss = { activeCelebration = null }
        )
    }
}

// Retained for tests and preview compatibility
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier, color = TextPrimary)
}
