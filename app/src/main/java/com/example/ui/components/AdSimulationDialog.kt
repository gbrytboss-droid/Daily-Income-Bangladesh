package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.ActiveAdSimulation
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.EmeraldReward
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun AdSimulationDialog(
    simulation: ActiveAdSimulation,
    onComplete: () -> Unit,
    onDismiss: () -> Unit
) {
    val totalSeconds = simulation.durationSeconds
    var secondsLeft by remember { mutableIntStateOf(totalSeconds) }
    var isCompleted by remember { mutableStateOf(false) }
    var showWarningEarlyClose by remember { mutableStateOf(false) }

    LaunchedEffect(simulation) {
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft -= 1
        }
        isCompleted = true
    }

    val progress = (totalSeconds - secondsLeft).toFloat() / totalSeconds.toFloat()
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800, easing = LinearEasing),
        label = "ad_progress"
    )

    val userShareCoins = (simulation.baseCoins * simulation.userSharePercent) / 100

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark),
            color = BackgroundDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(SurfaceCard)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isCompleted) EmeraldReward else GoldPrimary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isCompleted) "Reward Ready" else "Ad Playing • ${secondsLeft}s",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    IconButton(
                        onClick = { onDismiss() },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SurfaceCard)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Ad",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Progress Bar
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = GoldPrimary,
                    trackColor = SurfaceCard
                )

                // Warning Notice if user tried to close early
                AnimatedVisibility(visible = showWarningEarlyClose && !isCompleted) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF331418))
                            .border(1.dp, Color(0xFFFF5252), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "⚠️ Watch until the timer reaches 0s to receive your 50% coin reward!",
                            color = Color(0xFFFF8A80),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Main Interactive Ad Media Showcase
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 16.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    SurfaceDark,
                                    SurfaceCard,
                                    Color(0xFF191F38)
                                )
                            )
                        )
                        .border(1.dp, Color(0xFF2E355B), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(GoldLight, GoldDark)
                                    )
                                )
                        ) {
                            Icon(
                                imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.PlayCircleFilled,
                                contentDescription = null,
                                tint = BackgroundDark,
                                modifier = Modifier.size(54.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = simulation.title,
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Google AdMob Rewarded Partner",
                            color = GoldLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 50% Share Formula Display
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(BackgroundDark.copy(alpha = 0.6f))
                                .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Base Reward Pool",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "${simulation.baseCoins} 🪙",
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = "  × 50% =  ",
                                color = GoldPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Your Coin Share",
                                    color = EmeraldReward,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "+$userShareCoins 🪙",
                                    color = EmeraldReward,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }

                // Bottom Action Bar
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isCompleted) {
                        Button(
                            onClick = onComplete,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = BackgroundDark
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            val takaEarned = userShareCoins * 0.01
                            Text(
                                text = "CLAIM +$userShareCoins COINS (৳${String.format("%.2f", takaEarned)} TAKA)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Button(
                            onClick = {},
                            enabled = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                disabledContainerColor = SurfaceCard,
                                disabledContentColor = TextSecondary
                            )
                        ) {
                            Text(
                                text = "Earning reward in ${secondsLeft}s...",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        androidx.compose.material3.TextButton(
                            onClick = { onDismiss() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "বিজ্ঞাপন বন্ধ করুন (Exit Ad)",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "50% of ad value is directly credited to your coin wallet upon completion.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
