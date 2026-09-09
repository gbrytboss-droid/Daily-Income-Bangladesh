package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.RewardCelebration
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.EmeraldReward
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun AnimatedCoinBurst(
    celebration: RewardCelebration?,
    onDismiss: () -> Unit
) {
    if (celebration == null) return

    val scaleAnim = remember { Animatable(0.2f) }
    val alphaAnim = remember { Animatable(1f) }

    LaunchedEffect(celebration.timestamp) {
        scaleAnim.snapTo(0.2f)
        alphaAnim.snapTo(1f)
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
        )
        delay(1400)
        alphaAnim.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 300, easing = LinearEasing)
        )
        onDismiss()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "burst_rot"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onDismiss() }
            .background(Color.Black.copy(alpha = 0.65f * alphaAnim.value))
            .alpha(alphaAnim.value),
        contentAlignment = Alignment.Center
    ) {
        // Floating particles
        val particles = remember(celebration.timestamp) {
            List(12) { i ->
                val angle = (i * 30.0 + Random.nextDouble(-10.0, 10.0)) * (Math.PI / 180)
                val dist = Random.nextDouble(100.0, 180.0)
                Pair((cos(angle) * dist).toFloat(), (sin(angle) * dist).toFloat())
            }
        }

        particles.forEachIndexed { index, (x, y) ->
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (x * scaleAnim.value).roundToInt(),
                            (y * scaleAnim.value).roundToInt()
                        )
                    }
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(if (index % 2 == 0) GoldPrimary else AmberAccent)
                    .border(1.dp, GoldLight, CircleShape)
            )
        }

        // Central Celebration Card
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scaleAnim.value)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            BackgroundDark.copy(alpha = 0.95f),
                            Color(0xFF1E1A2E)
                        )
                    )
                )
                .border(2.dp, GoldPrimary, RoundedCornerShape(24.dp))
                .padding(horizontal = 32.dp, vertical = 24.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Stars,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(rotation)
                )
                Text(
                    text = "🪙",
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "+${celebration.coinsAwarded} COINS",
                color = GoldPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "50% Reward Share Credited!",
                    color = EmeraldReward,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${celebration.title} (Base: ${celebration.baseCoins} coins)",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}
