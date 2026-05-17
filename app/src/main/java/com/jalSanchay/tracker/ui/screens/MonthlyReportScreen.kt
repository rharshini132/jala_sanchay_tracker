package com.jalSanchay.tracker.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jalSanchay.tracker.ui.theme.*
import com.jalSanchay.tracker.viewmodel.ReportViewModel
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyReportScreen(
    viewModel: ReportViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val currentMonth by viewModel.currentMonth.collectAsState()
    val monthEntries by viewModel.monthEntries.collectAsState()
    val prevMonthEntries by viewModel.prevMonthEntries.collectAsState()
    val userId by viewModel.userId.collectAsState()
    val context = LocalContext.current

    val totalRainfall = monthEntries.sumOf { it.rainfallMm.toDouble() }.toFloat()
    val totalHarvest = monthEntries.sumOf { it.litersHarvested.toDouble() }.toFloat()
    val avgDaily = if (currentMonth.lengthOfMonth() > 0) totalHarvest / currentMonth.lengthOfMonth() else 0f
    val peakEntry = monthEntries.maxByOrNull { it.rainfallMm }
    val eventCount = monthEntries.size

    val prevTotalRainfall = prevMonthEntries.sumOf { it.rainfallMm.toDouble() }.toFloat()
    val prevTotalHarvest = prevMonthEntries.sumOf { it.litersHarvested.toDouble() }.toFloat()

    val rainfallDiff = totalRainfall - prevTotalRainfall
    val harvestDiff = totalHarvest - prevTotalHarvest
    val rainfallPct = if (prevTotalRainfall > 0) (rainfallDiff / prevTotalRainfall * 100) else 0f
    val harvestPct = if (prevTotalHarvest > 0) (harvestDiff / prevTotalHarvest * 100) else 0f

    // Chart data: daily liters
    val dailyData = remember(monthEntries) {
        val map = mutableMapOf<Int, Float>()
        monthEntries.forEach { entry ->
            try {
                val day = entry.date.split("-").last().toInt()
                map[day] = (map[day] ?: 0f) + entry.litersHarvested
            } catch (_: Exception) {}
        }
        map
    }

    val maxDailyValue = dailyData.values.maxOrNull() ?: 1f

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Month selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { userId?.let { viewModel.previousMonth(it) } }) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous month")
            }
            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
            IconButton(onClick = { userId?.let { viewModel.nextMonth(it) } }) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next month")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Month Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📊 Month Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(12.dp))
                SummaryRow("Total Rainfall", "${"%.1f".format(totalRainfall)} mm")
                SummaryRow("Total Harvested", "${"%.1f".format(totalHarvest)} L")
                SummaryRow("Avg Daily Harvest", "${"%.1f".format(avgDaily)} L")
                SummaryRow("Peak Rainfall Day", peakEntry?.let { "${it.date} (${it.rainfallMm} mm)" } ?: "N/A")
                SummaryRow("Rainfall Events", "$eventCount")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Bar Chart (Canvas-drawn)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Daily Harvest", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(12.dp))

                var animPlayed by remember { mutableStateOf(false) }
                val animProgress by animateFloatAsState(
                    targetValue = if (animPlayed) 1f else 0f,
                    animationSpec = tween(1200, easing = EaseOutCubic),
                    label = "barAnim"
                )
                LaunchedEffect(monthEntries) { animPlayed = true }

                Canvas(
                    modifier = Modifier.fillMaxWidth().height(180.dp)
                ) {
                    val daysInMonth = currentMonth.lengthOfMonth()
                    val barWidth = size.width / (daysInMonth + 2)
                    val chartHeight = size.height - 20f

                    for (day in 1..daysInMonth) {
                        val value = dailyData[day] ?: 0f
                        val barHeight = if (maxDailyValue > 0) (value / maxDailyValue * chartHeight * animProgress) else 0f
                        val x = (day) * barWidth
                        val color = if (value == maxDailyValue && value > 0) Accent else Primary

                        drawRect(
                            color = color,
                            topLeft = Offset(x, chartHeight - barHeight),
                            size = Size(barWidth * 0.7f, barHeight)
                        )
                    }

                    // X axis
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.3f),
                        start = Offset(0f, chartHeight),
                        end = Offset(size.width, chartHeight),
                        strokeWidth = 1f
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Cumulative Line Chart
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Cumulative Savings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(12.dp))

                val cumulativeData = remember(dailyData) {
                    val sorted = dailyData.toSortedMap()
                    var cumSum = 0f
                    sorted.map { (day, value) -> cumSum += value; day to cumSum }
                }
                val maxCum = cumulativeData.lastOrNull()?.second ?: 1f

                Canvas(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                    val daysInMonth = currentMonth.lengthOfMonth()
                    if (cumulativeData.size >= 2) {
                        for (i in 0 until cumulativeData.size - 1) {
                            val (d1, v1) = cumulativeData[i]
                            val (d2, v2) = cumulativeData[i + 1]
                            val x1 = d1.toFloat() / daysInMonth * size.width
                            val y1 = size.height - (v1 / maxCum * size.height * 0.9f)
                            val x2 = d2.toFloat() / daysInMonth * size.width
                            val y2 = size.height - (v2 / maxCum * size.height * 0.9f)
                            drawLine(color = SuccessWater, start = Offset(x1, y1), end = Offset(x2, y2), strokeWidth = 3f)
                        }
                        cumulativeData.forEach { (d, v) ->
                            val x = d.toFloat() / daysInMonth * size.width
                            val y = size.height - (v / maxCum * size.height * 0.9f)
                            drawCircle(color = SuccessWater, radius = 4f, center = Offset(x, y))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Month Comparison Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📈 vs Last Month", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(12.dp))
                ComparisonRow("Rainfall", rainfallDiff, rainfallPct, "mm")
                ComparisonRow("Harvest", harvestDiff, harvestPct, "L")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 6. Share Report
        Button(
            onClick = {
                val text = viewModel.generateShareText()
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, text)
                }
                context.startActivity(Intent.createChooser(intent, "Share Report"))
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Share Report", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 7. Export CSV
        OutlinedButton(
            onClick = {
                val csv = viewModel.generateCsv()
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_TEXT, csv)
                    putExtra(Intent.EXTRA_SUBJECT, "Jal-Sanchay Report ${currentMonth}")
                }
                context.startActivity(Intent.createChooser(intent, "Export CSV"))
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Export as CSV")
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ComparisonRow(label: String, diff: Float, pct: Float, unit: String) {
    val isPositive = diff >= 0
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                contentDescription = null,
                tint = if (isPositive) SuccessWater else Error,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${if (isPositive) "+" else ""}${"%.1f".format(diff)} $unit (${"%.0f".format(pct)}%)",
                style = MaterialTheme.typography.bodySmall,
                color = if (isPositive) SuccessWater else Error,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
