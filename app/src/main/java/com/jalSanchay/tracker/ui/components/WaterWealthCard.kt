package com.jalSanchay.tracker.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jalSanchay.tracker.ui.theme.Accent
import com.jalSanchay.tracker.ui.theme.Primary
import com.jalSanchay.tracker.ui.theme.Secondary

@Composable
fun WaterWealthCard(
    todayHarvest: Float,
    totalSavings: Float,
    tankPercent: Float,
    modifier: Modifier = Modifier
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedToday by animateFloatAsState(
        targetValue = if (animationPlayed) todayHarvest else 0f,
        animationSpec = tween(1200, easing = EaseOutCubic),
        label = "today"
    )
    val animatedTotal by animateFloatAsState(
        targetValue = if (animationPlayed) totalSavings else 0f,
        animationSpec = tween(1500, easing = EaseOutCubic),
        label = "total"
    )
    val animatedTank by animateFloatAsState(
        targetValue = if (animationPlayed) tankPercent else 0f,
        animationSpec = tween(1800, easing = EaseOutCubic),
        label = "tank"
    )

    LaunchedEffect(Unit) { animationPlayed = true }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Secondary, Primary, Accent)
                )
            )
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatItem(
                label = "Today's Harvest",
                value = "${"%.1f".format(animatedToday)} L",
                modifier = Modifier.weight(1f)
            )
            StatItem(
                label = "Total Savings",
                value = "${"%.0f".format(animatedTotal)} L",
                modifier = Modifier.weight(1f)
            )
            StatItem(
                label = "Tank Level",
                value = "${"%.0f".format(animatedTank)}%",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.White
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f)
            )
        )
    }
}
