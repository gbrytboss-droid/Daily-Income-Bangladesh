package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.admob.AdMobConfig
import com.example.data.db.UserProfile
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.EmeraldReward
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun EarnScreen(
    profile: UserProfile?,
    adMobConfig: AdMobConfig,
    isAdLoaded: Boolean,
    adStatusMessage: String,
    onWatchAd: (activity: Activity, title: String, adType: String, baseCoins: Int, sharePercent: Int) -> Unit,
    onClaimStreak: () -> Unit,
    onOpenAdMobConfig: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scrollState = rememberScrollState()

    val coins = profile?.coins ?: 0L
    val adsWatched = profile?.adsWatched ?: 0
    val streakDays = profile?.streakDays ?: 1

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(scrollState)
            .padding(bottom = 90.dp)
    ) {
        // Top AdMob Config Pill Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceCard)
                    .clickable { onOpenAdMobConfig() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VpnKey,
                    contentDescription = null,
                    tint = if (adMobConfig.isCustomKeyApplied) EmeraldReward else GoldPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (adMobConfig.isCustomKeyApplied) "Custom AdMob Key" else "AdMob Test Mode",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isAdLoaded) Color(0xFF132E1D) else SurfaceCard)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isAdLoaded) EmeraldReward else AmberAccent)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isAdLoaded) "Ad Ready" else "AdMob Active",
                    color = if (isAdLoaded) EmeraldReward else TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Hero Banner Graphic
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(22.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.hero_reward_banner),
                contentDescription = "Reward Vault",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.85f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(18.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column {
                    Text(
                        text = "DAILY INCOME BANGLADESH",
                        color = GoldPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "প্রতি Ad এ পাবেন ৳০.৫০ টাকা\n(৫০% গ্যারান্টিড শেয়ার)",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 24.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Main Coin Balance Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .testTag("balance_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                Brush.horizontalGradient(listOf(GoldLight, AmberAccent, GoldDark))
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "YOUR COIN BALANCE",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceCard)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = AmberAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Streak: Day $streakDays",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🪙",
                        fontSize = 36.sp,
                        modifier = Modifier.scale(pulseScale)
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    AnimatedContent(
                        targetState = coins,
                        transitionSpec = {
                            (slideInVertically { height -> height } + fadeIn()) togetherWith
                                    (slideOutVertically { height -> -height } + fadeOut())
                        },
                        label = "coin_counter"
                    ) { targetCoins ->
                        Text(
                            text = String.format("%,d", targetCoins),
                            color = GoldPrimary,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "COINS",
                        color = GoldLight,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val takaEst = (coins * 0.01)
                    Column {
                        Text(
                            text = "≈ ৳${String.format("%.2f", takaEst)} Taka (টাকা)",
                            color = EmeraldReward,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "রেট: প্রতি Ad = ৳০.৫০ টাকা",
                            color = GoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = "$adsWatched ads viewed",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Featured AdMob Rewarded Ad Card
        Text(
            text = "Featured AdMob Rewards",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "AdMob Rewarded Video",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Pool: 100 🪙 • আপনার শেয়ার: ৫০% (৳০.৫০ টাকা)",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = EmeraldReward.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldReward.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "+৳0.50 🪙",
                            color = EmeraldReward,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        activity?.let {
                            onWatchAd(it, "AdMob Rewarded Video", "admob_rewarded", 100, 50)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("watch_admob_ad_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = BackgroundDark
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "WATCH AD & EARN ৳0.50 TAKA (+50 🪙)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Multiplier & Bonus Tasks
        Text(
            text = "Bonus Ad Opportunities",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Sponsor Ad (200 pool -> 100 share)
        BonusAdRow(
            title = "Sponsor High-Yield Video",
            basePool = 200,
            userCoins = 100,
            badge = "2X POOL",
            badgeColor = AmberAccent,
            onWatch = {
                activity?.let {
                    onWatchAd(it, "Sponsor High-Yield Video", "sponsor_ad", 200, 50)
                }
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Mega VIP Ad (300 pool -> 150 share)
        BonusAdRow(
            title = "Mega Sponsor Video",
            basePool = 300,
            userCoins = 150,
            badge = "MEGA",
            badgeColor = GoldLight,
            onWatch = {
                activity?.let {
                    onWatchAd(it, "Mega Sponsor Video", "vip_ad", 300, 50)
                }
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Daily Check-In Bonus
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceElevated)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(AmberAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = AmberAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Daily Check-In Streak",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Keep your streak alive for multipliers",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Button(
                    onClick = onClaimStreak,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberAccent,
                        contentColor = BackgroundDark
                    )
                ) {
                    Text("Claim", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun BonusAdRow(
    title: String,
    basePool: Int,
    userCoins: Int,
    badge: String,
    badgeColor: Color,
    onWatch: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceElevated)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(badgeColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Bolt,
                        contentDescription = null,
                        tint = badgeColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = badge,
                            color = badgeColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    val takaVal = userCoins * 0.01
                    Text(
                        text = "Pool: $basePool 🪙 • ৫০% = +$userCoins 🪙 (৳${String.format("%.2f", takaVal)})",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Button(
                onClick = onWatch,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = BackgroundDark
                )
            ) {
                val takaVal = userCoins * 0.01
                Text(
                    text = "+৳${String.format("%.2f", takaVal)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
