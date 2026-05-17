package com.jalSanchay.tracker.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jalSanchay.tracker.ui.theme.*
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun TankVisualization(
    currentLevel: Float,
    capacity: Float,
    modifier: Modifier = Modifier
) {
    val percentage = if (capacity > 0) (currentLevel / capacity * 100).coerceIn(0f, 100f) else 0f
    val fillFraction = percentage / 100f

    val animatedFill by animateFloatAsState(
        targetValue = fillFraction,
        animationSpec = tween(durationMillis = 1500, easing = EaseOutCubic),
        label = "tankFill"
    )

    val waterColor = when {
        percentage <= 25f -> TankCritical
        percentage <= 50f -> TankLow
        percentage <= 75f -> TankGood
        else -> TankFull
    }

    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )

    val bubbles = remember {
        List(6) {
            BubbleState(
                x = Random.nextFloat(),
                startY = Random.nextFloat() * 0.3f,
                radius = Random.nextFloat() * 6f + 3f,
                speed = Random.nextFloat() * 0.3f + 0.2f
            )
        }
    }

    val bubbleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bubbles"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(180.dp)
                .height(220.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val tankPadding = 10f
                val tankLeft = tankPadding
                val tankTop = tankPadding
                val tankWidth = size.width - tankPadding * 2
                val tankHeight = size.height - tankPadding * 2
                val cornerRadius = 24f

                // Tank outline
                val tankPath = Path().apply {
                    addRoundRect(
                        androidx.compose.ui.geometry.RoundRect(
                            left = tankLeft,
                            top = tankTop,
                            right = tankLeft + tankWidth,
                            bottom = tankTop + tankHeight,
                            topLeftCornerRadius = CornerRadius(8f, 8f),
                            topRightCornerRadius = CornerRadius(8f, 8f),
                            bottomLeftCornerRadius = CornerRadius(cornerRadius, cornerRadius),
                            bottomRightCornerRadius = CornerRadius(cornerRadius, cornerRadius)
                        )
                    )
                }

                // Water fill with wave
                val waterTop = tankTop + tankHeight * (1f - animatedFill)

                clipPath(tankPath) {
                    // Water body
                    val waterPath = Path().apply {
                        moveTo(tankLeft, waterTop)
                        var x = tankLeft
                        while (x <= tankLeft + tankWidth) {
                            val waveY = waterTop + sin((x / tankWidth * 4 + waveOffset / 57.3f).toDouble()).toFloat() * 4f
                            lineTo(x, waveY)
                            x += 2f
                        }
                        lineTo(tankLeft + tankWidth, tankTop + tankHeight)
                        lineTo(tankLeft, tankTop + tankHeight)
                        close()
                    }
                    drawPath(waterPath, waterColor.copy(alpha = 0.7f))

                    // Second wave layer
                    val waterPath2 = Path().apply {
                        moveTo(tankLeft, waterTop + 3f)
                        var x2 = tankLeft
                        while (x2 <= tankLeft + tankWidth) {
                            val waveY = waterTop + 3f + sin((x2 / tankWidth * 3 + waveOffset / 57.3f + 2f).toDouble()).toFloat() * 3f
                            lineTo(x2, waveY)
                            x2 += 2f
                        }
                        lineTo(tankLeft + tankWidth, tankTop + tankHeight)
                        lineTo(tankLeft, tankTop + tankHeight)
                        close()
                    }
                    drawPath(waterPath2, waterColor.copy(alpha = 0.4f))

                    // Bubbles
                    if (animatedFill > 0.05f) {
                        bubbles.forEach { bubble ->
                            val bx = tankLeft + bubble.x * tankWidth
                            val animY = (bubble.startY + bubbleProgress * bubble.speed) % 1f
                            val by = waterTop + (1f - animY) * (tankTop + tankHeight - waterTop)
                            if (by > waterTop && by < tankTop + tankHeight) {
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.3f),
                                    radius = bubble.radius,
                                    center = Offset(bx, by)
                                )
                            }
                        }
                    }
                }

                // Tank border
                drawPath(tankPath, color = waterColor, style = Stroke(width = 3f))

                // Tick marks on the side
                for (i in 1..3) {
                    val tickY = tankTop + tankHeight * (i / 4f)
                    drawLine(
                        color = waterColor.copy(alpha = 0.4f),
                        start = Offset(tankLeft, tickY),
                        end = Offset(tankLeft + 15f, tickY),
                        strokeWidth = 1.5f
                    )
                }
            }

            // Percentage text in center
            Text(
                text = "${percentage.toInt()}%",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = if (percentage > 50f) Color.White else MaterialTheme.colorScheme.onBackground
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${"%.0f".format(currentLevel)} L / ${"%.0f".format(capacity)} L capacity",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

private data class BubbleState(
    val x: Float,
    val startY: Float,
    val radius: Float,
    val speed: Float
)
