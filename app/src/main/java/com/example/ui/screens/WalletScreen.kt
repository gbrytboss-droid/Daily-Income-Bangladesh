package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.RewardTransaction
import com.example.data.db.UserProfile
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.EmeraldReward
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WalletScreen(
    profile: UserProfile?,
    transactions: List<RewardTransaction>,
    onOpenWithdraw: () -> Unit
) {
    val coins = profile?.coins ?: 0L
    val totalCoins = profile?.totalCoinsEarned ?: 0L
    val adsWatched = profile?.adsWatched ?: 0

    val milestoneTarget = 50000L // 50,000 coins = 500 Taka
    val milestoneProgress = (coins.toFloat() / milestoneTarget.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(bottom = 85.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(GoldPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Daily Income Wallet (টাকা ওয়ালেট)",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "৫০% নিশ্চিত শেয়ার • bKash, Nagad & Recharge",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("transactions_list"),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp)
        ) {
            item {
                // Wallet Stats Summary Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Current Balance",
                        value = "$coins 🪙",
                        subtitle = "≈ ৳${String.format("%.2f", coins * 0.01)} Taka (টাকা)",
                        accentColor = GoldPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "Total Earned",
                        value = "$totalCoins 🪙",
                        subtitle = "≈ ৳${String.format("%.2f", totalCoins * 0.01)} Taka • $adsWatched Ads",
                        accentColor = EmeraldReward,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Milestone Redemption Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Minimum Withdrawal: ৳500 টাকা",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "৫০,০০০ কয়েন = ৫০০ টাকা (bKash/Nagad)",
                                    color = GoldLight,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = "$coins / $milestoneTarget 🪙",
                                color = GoldPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { milestoneProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(9.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (coins >= milestoneTarget) EmeraldReward else GoldPrimary,
                            trackColor = SurfaceElevated
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val percentage = (milestoneProgress * 100).toInt()
                        Text(
                            text = "$percentage% সম্পন্ন • বর্তমান মূল্য: ৳${String.format("%.2f", coins * 0.01)} টাকা (বিকাশ/নগদ/রকেট)",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        androidx.compose.material3.Button(
                            onClick = onOpenWithdraw,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("open_withdraw_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = if (coins >= milestoneTarget) EmeraldReward else GoldPrimary,
                                contentColor = BackgroundDark
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (coins >= milestoneTarget) "উইথড্র রিকোয়েস্ট পাঠান (৳${String.format("%.2f", coins * 0.01)})" else "টাকা উত্তোলন করুন (Withdrawal)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Transactions Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Transactions (${transactions.size})",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.PieChart,
                            contentDescription = null,
                            tint = EmeraldReward,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "50% Share Rate",
                            color = EmeraldReward,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            if (transactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No Transactions Yet",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Watch your first AdMob ad on the Earn screen to get 50% coins credited!",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                items(transactions, key = { it.id }) { tx ->
                    TransactionItemRow(transaction = tx)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceElevated)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = accentColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun TransactionItemRow(transaction: RewardTransaction) {
    val formatter = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
    val dateString = formatter.format(Date(transaction.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceElevated)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = transaction.title,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${transaction.baseCoins} pool × ${transaction.userSharePercent}% share • $dateString",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = EmeraldReward.copy(alpha = 0.15f)
            ) {
                val takaVal = transaction.coinsEarned * 0.01
                Text(
                    text = "+${transaction.coinsEarned} 🪙 (৳${String.format("%.2f", takaVal)})",
                    color = EmeraldReward,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
