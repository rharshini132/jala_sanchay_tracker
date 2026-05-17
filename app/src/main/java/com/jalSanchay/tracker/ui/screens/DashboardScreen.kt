package com.jalSanchay.tracker.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jalSanchay.tracker.ui.components.*
import com.jalSanchay.tracker.ui.theme.Accent
import com.jalSanchay.tracker.ui.theme.Primary
import com.jalSanchay.tracker.ui.theme.SuccessWater
import com.jalSanchay.tracker.viewmodel.DashboardViewModel
import com.jalSanchay.tracker.viewmodel.HistoryViewModel
import com.jalSanchay.tracker.viewmodel.ReportViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToLogRainfall: () -> Unit,
    onNavigateToMonthlyReport: () -> Unit,
    onNavigateToTips: () -> Unit,
    dashboardViewModel: DashboardViewModel = hiltViewModel(),
    historyViewModel: HistoryViewModel = hiltViewModel(),
    reportViewModel: ReportViewModel = hiltViewModel()
) {
    val userId by dashboardViewModel.userId.collectAsState()
    val user by dashboardViewModel.user.collectAsState()
    val tankSetup by dashboardViewModel.tankSetup.collectAsState()
    val recentEntries by dashboardViewModel.recentEntries.collectAsState()
    val totalHarvest by dashboardViewModel.totalHarvest.collectAsState()
    val todayHarvest by dashboardViewModel.todayHarvest.collectAsState()
    val weekHarvest by dashboardViewModel.weekHarvest.collectAsState()
    val monthHarvest by dashboardViewModel.monthHarvest.collectAsState()
    val darkMode by dashboardViewModel.darkMode.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(userId) {
        userId?.let { uid ->
            dashboardViewModel.loadDashboard(uid)
            historyViewModel.loadEntries(uid)
            reportViewModel.loadMonth(uid)
        }
    }

    val greeting = remember {
        val hour = LocalTime.now().hour
        when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("💧 Jal-Sanchay", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Primary)
                        Text(
                            "$greeting, ${user?.name?.split(" ")?.firstOrNull() ?: "User"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = Primary)
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            BottomNavBar(
                selectedIndex = selectedTab,
                onItemSelected = { selectedTab = it }
            )
        }
    ) { padding ->
        when (selectedTab) {
            0 -> HomeTab(
                modifier = Modifier.padding(padding),
                todayHarvest = todayHarvest,
                totalHarvest = totalHarvest,
                tankSetup = tankSetup,
                weekHarvest = weekHarvest,
                monthHarvest = monthHarvest,
                recentEntries = recentEntries,
                dailyNeed = 135f,
                onLogRainfall = onNavigateToLogRainfall
            )
            1 -> LogRainfallScreen(
                onBack = { selectedTab = 0 },
                onSaved = { selectedTab = 0 },
                modifier = Modifier.padding(padding)
            )
            2 -> HistoryScreen(
                viewModel = historyViewModel,
                onNavigateToLogRainfall = onNavigateToLogRainfall,
                modifier = Modifier.padding(padding)
            )
            3 -> MonthlyReportScreen(
                viewModel = reportViewModel,
                modifier = Modifier.padding(padding)
            )
            4 -> ImpactScreen(
                totalHarvest = totalHarvest,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun HomeTab(
    modifier: Modifier = Modifier,
    todayHarvest: Float,
    totalHarvest: Float,
    tankSetup: com.jalSanchay.tracker.data.model.TankSetup?,
    weekHarvest: Float,
    monthHarvest: Float,
    recentEntries: List<com.jalSanchay.tracker.data.model.RainfallEntry>,
    dailyNeed: Float,
    onLogRainfall: () -> Unit
) {
    val tankPercent = if (tankSetup != null && tankSetup.tankCapacityLiters > 0) {
        (tankSetup.currentWaterLevelLiters / tankSetup.tankCapacityLiters * 100)
    } else 0f

    val daysOfSupply = if (dailyNeed > 0) totalHarvest / dailyNeed else 0f

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // 1. Water Wealth Hero Card
        WaterWealthCard(
            todayHarvest = todayHarvest,
            totalSavings = totalHarvest,
            tankPercent = tankPercent
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Tank Visualization
        if (tankSetup != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Tank Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    TankVisualization(
                        currentLevel = tankSetup.currentWaterLevelLiters,
                        capacity = tankSetup.tankCapacityLiters
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. Quick Stats Row
        Text("Quick Stats", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                QuickStatCard(label = "This Week", value = "${"%.1f".format(weekHarvest)} L", icon = "📅")
            }
            item {
                QuickStatCard(label = "This Month", value = "${"%.1f".format(monthHarvest)} L", icon = "📊")
            }
            item {
                QuickStatCard(label = "Days Supply", value = "${"%.1f".format(daysOfSupply)}", icon = "⏱")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4. Log Today's Rainfall button
        Button(
            onClick = onLogRainfall,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SuccessWater)
        ) {
            Icon(Icons.Default.WaterDrop, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log Today's Rainfall", fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 5. Recent Entries
        if (recentEntries.isNotEmpty()) {
            Text("Recent Entries", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            recentEntries.forEach { entry ->
                RainfallHistoryCard(entry = entry)
                Spacer(modifier = Modifier.height(8.dp))
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("💧", style = MaterialTheme.typography.displayMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No entries yet", style = MaterialTheme.typography.titleSmall)
                    Text("Start logging your rainfall data!", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun QuickStatCard(label: String, value: String, icon: String) {
    Card(
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Primary)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}
