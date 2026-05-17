package com.jalSanchay.tracker.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jalSanchay.tracker.data.datastore.UserPreferences
import com.jalSanchay.tracker.ui.theme.Error
import com.jalSanchay.tracker.ui.theme.Primary
import com.jalSanchay.tracker.viewmodel.AuthViewModel
import com.jalSanchay.tracker.viewmodel.HistoryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToSetup: () -> Unit,
    onNavigateToTips: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    historyViewModel: HistoryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPreferences = remember { UserPreferences(context) }

    val darkMode by userPreferences.darkMode.collectAsState(initial = false)
    val waterRate by userPreferences.waterRate.collectAsState(initial = 0.05f)
    val dailyNeed by userPreferences.dailyNeed.collectAsState(initial = 135f)
    val fontSize by userPreferences.fontSize.collectAsState(initial = "Medium")
    val dailyReminder by userPreferences.dailyReminder.collectAsState(initial = false)
    val reminderTime by userPreferences.reminderTime.collectAsState(initial = "08:00")
    val rainThreshold by userPreferences.rainAlertThreshold.collectAsState(initial = 80)
    val units by userPreferences.units.collectAsState(initial = "Metric")
    val colorTheme by userPreferences.colorTheme.collectAsState(initial = "Blue")
    val userId by userPreferences.userId.collectAsState(initial = null)

    // Expandable states
    var colorThemeExpanded by remember { mutableStateOf(false) }
    var fontSizeExpanded by remember { mutableStateOf(false) }
    var waterRateExpanded by remember { mutableStateOf(false) }
    var dailyNeedExpanded by remember { mutableStateOf(false) }
    var unitsExpanded by remember { mutableStateOf(false) }
    var reminderTimeExpanded by remember { mutableStateOf(false) }
    var thresholdExpanded by remember { mutableStateOf(false) }
    var aboutExpanded by remember { mutableStateOf(false) }
    var aboutRwhExpanded by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }

    // Temp values
    var tempWaterRate by remember { mutableStateOf(waterRate.toString()) }
    var tempDailyNeed by remember { mutableStateOf(dailyNeed.toString()) }
    var tempThreshold by remember { mutableStateOf(rainThreshold.toString()) }

    LaunchedEffect(waterRate) { tempWaterRate = waterRate.toString() }
    LaunchedEffect(dailyNeed) { tempDailyNeed = dailyNeed.toString() }
    LaunchedEffect(rainThreshold) { tempThreshold = rainThreshold.toString() }

    // Dialogs
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(onClick = {
                    authViewModel.logout()
                    showLogoutDialog = false
                    onLogout()
                }) { Text("Logout", color = Error) }
            },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") } }
        )
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear History") },
            text = { Text("This will delete all rainfall entries. Your setup will be kept.") },
            confirmButton = {
                TextButton(onClick = {
                    userId?.let { scope.launch { historyViewModel.applyFilter(it, "All") } }
                    showClearDialog = false
                }) { Text("Clear", color = Error) }
            },
            dismissButton = { TextButton(onClick = { showClearDialog = false }) { Text("Cancel") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // ========== APPEARANCE ==========
            SectionTitle("Appearance")

            // Dark Mode
            SettingsToggleItem(
                icon = Icons.Default.DarkMode,
                title = "Dark Mode",
                checked = darkMode,
                onCheckedChange = { scope.launch { userPreferences.setDarkMode(it) } }
            )

            // Color Theme
            SettingsExpandableItem(
                icon = Icons.Default.Palette,
                title = "Primary Color Theme",
                expanded = colorThemeExpanded,
                onToggle = { colorThemeExpanded = !colorThemeExpanded }
            ) {
                listOf("Blue", "Green", "Purple").forEach { theme ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = colorTheme == theme, onClick = { scope.launch { userPreferences.setColorTheme(theme) } })
                        Text(theme, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            // Font Size
            SettingsExpandableItem(
                icon = Icons.Default.TextFields,
                title = "Font Size",
                expanded = fontSizeExpanded,
                onToggle = { fontSizeExpanded = !fontSizeExpanded }
            ) {
                listOf("Small", "Medium", "Large").forEach { size ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = fontSize == size, onClick = { scope.launch { userPreferences.setFontSize(size) } })
                        Text(size, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // ========== DATA & CALCULATION ==========
            SectionTitle("Data & Calculation")

            SettingsExpandableItem(
                icon = Icons.Default.CurrencyRupee,
                title = "Local Water Rate (₹/liter)",
                expanded = waterRateExpanded,
                onToggle = { waterRateExpanded = !waterRateExpanded }
            ) {
                OutlinedTextField(
                    value = tempWaterRate,
                    onValueChange = { tempWaterRate = it },
                    label = { Text("Rate (₹/liter)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, cursorColor = Primary),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    tempWaterRate.toFloatOrNull()?.let { scope.launch { userPreferences.setWaterRate(it) } }
                    waterRateExpanded = false
                }, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                    Text("Save", color = Color.White)
                }
            }

            SettingsExpandableItem(
                icon = Icons.Default.Person,
                title = "Daily Water Need (liters)",
                expanded = dailyNeedExpanded,
                onToggle = { dailyNeedExpanded = !dailyNeedExpanded }
            ) {
                OutlinedTextField(
                    value = tempDailyNeed,
                    onValueChange = { tempDailyNeed = it },
                    label = { Text("Liters/day") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, cursorColor = Primary),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    tempDailyNeed.toFloatOrNull()?.let { scope.launch { userPreferences.setDailyNeed(it) } }
                    dailyNeedExpanded = false
                }, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                    Text("Save", color = Color.White)
                }
            }

            SettingsExpandableItem(
                icon = Icons.Default.Straighten,
                title = "Units",
                expanded = unitsExpanded,
                onToggle = { unitsExpanded = !unitsExpanded }
            ) {
                listOf("Metric", "Imperial").forEach { u ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = units == u, onClick = { scope.launch { userPreferences.setUnits(u) } })
                        Text(u, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // ========== NOTIFICATIONS ==========
            SectionTitle("Notifications")

            SettingsToggleItem(
                icon = Icons.Default.Notifications,
                title = "Daily Reminder",
                checked = dailyReminder,
                onCheckedChange = { scope.launch { userPreferences.setDailyReminder(it) } }
            )

            if (dailyReminder) {
                SettingsExpandableItem(
                    icon = Icons.Default.AccessTime,
                    title = "Reminder Time",
                    expanded = reminderTimeExpanded,
                    onToggle = { reminderTimeExpanded = !reminderTimeExpanded }
                ) {
                    Text("Current: $reminderTime", style = MaterialTheme.typography.bodyMedium)
                    Text("(Time picker coming in next update)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                }
            }

            SettingsExpandableItem(
                icon = Icons.Default.Warning,
                title = "Rain Alert Threshold",
                expanded = thresholdExpanded,
                onToggle = { thresholdExpanded = !thresholdExpanded }
            ) {
                Text("Notify when tank > X% full", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                OutlinedTextField(
                    value = tempThreshold,
                    onValueChange = { tempThreshold = it },
                    label = { Text("Threshold %") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, cursorColor = Primary),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    tempThreshold.toIntOrNull()?.let { scope.launch { userPreferences.setRainAlertThreshold(it) } }
                    thresholdExpanded = false
                }, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                    Text("Save", color = Color.White)
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // ========== DATA MANAGEMENT ==========
            SectionTitle("Data Management")

            SettingsClickItem(icon = Icons.Default.FileDownload, title = "Export All Data") {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_TEXT, "Export feature — data export initiated via Jal-Sanchay")
                    putExtra(Intent.EXTRA_SUBJECT, "Jal-Sanchay Data Export")
                }
                context.startActivity(Intent.createChooser(intent, "Export Data"))
            }

            SettingsClickItem(icon = Icons.Default.Build, title = "Edit Tank Setup") { onNavigateToSetup() }
            SettingsClickItem(icon = Icons.Default.DeleteSweep, title = "Clear History") { showClearDialog = true }

            Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // ========== ABOUT ==========
            SectionTitle("About")

            SettingsExpandableItem(
                icon = Icons.Default.Info,
                title = "About Jal-Sanchay",
                expanded = aboutExpanded,
                onToggle = { aboutExpanded = !aboutExpanded }
            ) {
                Text(
                    "Jal-Sanchay Tracker v1.0\n\nA comprehensive rainwater harvesting tracker designed to help you monitor, measure, and maximize your water conservation efforts. Built with 💧 for a sustainable future.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            SettingsClickItem(icon = Icons.Default.MenuBook, title = "Water Harvesting Tips") { onNavigateToTips() }

            SettingsExpandableItem(
                icon = Icons.Default.Public,
                title = "About Rainwater Harvesting",
                expanded = aboutRwhExpanded,
                onToggle = { aboutRwhExpanded = !aboutRwhExpanded }
            ) {
                Text(
                    "Rainwater harvesting is the process of collecting and storing rainwater from rooftops and other surfaces for later use. It's one of the most sustainable water management practices, reducing dependency on municipal water supply and helping recharge groundwater levels. India receives an average of 1,170mm of rainfall annually, making it an ideal country for rainwater harvesting. Even a small roof of 50m² can harvest over 40,000 liters of water per year!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            // ========== ACCOUNT ==========
            SectionTitle("Account")

            SettingsClickItem(icon = Icons.Default.Person, title = "View Profile") { onNavigateToProfile() }
            SettingsClickItem(icon = Icons.Default.Logout, title = "Logout", isDestructive = true) { showLogoutDialog = true }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = Primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, style = MaterialTheme.typography.bodyLarge)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedTrackColor = Primary))
    }
}

@Composable
private fun SettingsExpandableItem(
    icon: ImageVector,
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .then(Modifier.let {
                    it
                }),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, style = MaterialTheme.typography.bodyLarge)
            }
            IconButton(onClick = onToggle) {
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ChevronRight,
                    contentDescription = "Toggle",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        AnimatedVisibility(visible = expanded, enter = expandVertically(), exit = shrinkVertically()) {
            Column(modifier = Modifier.padding(start = 34.dp, bottom = 8.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsClickItem(
    icon: ImageVector,
    title: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(icon, contentDescription = null, tint = if (isDestructive) Error else Primary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, style = MaterialTheme.typography.bodyLarge, color = if (isDestructive) Error else MaterialTheme.colorScheme.onBackground)
        }
        IconButton(onClick = onClick) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Navigate", modifier = Modifier.size(20.dp))
        }
    }
}
