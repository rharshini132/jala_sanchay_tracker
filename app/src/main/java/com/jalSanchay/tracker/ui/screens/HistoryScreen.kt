package com.jalSanchay.tracker.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jalSanchay.tracker.data.model.RainfallEntry
import com.jalSanchay.tracker.ui.components.RainfallHistoryCard
import com.jalSanchay.tracker.ui.theme.Primary
import com.jalSanchay.tracker.ui.theme.SuccessWater
import com.jalSanchay.tracker.viewmodel.HistoryViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    onNavigateToLogRainfall: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val entries by viewModel.entries.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val userId by viewModel.userId.collectAsState()

    var showDeleteDialog by remember { mutableStateOf<RainfallEntry?>(null) }
    var showEditSheet by remember { mutableStateOf<RainfallEntry?>(null) }
    var showCustomFrom by remember { mutableStateOf(false) }
    var showCustomTo by remember { mutableStateOf(false) }
    var customFrom by remember { mutableStateOf("") }
    var customTo by remember { mutableStateOf("") }

    val filters = listOf("All", "This Week", "This Month", "Last 3 Months", "Custom Range")

    val totalRainfall = entries.sumOf { it.rainfallMm.toDouble() }.toFloat()
    val totalHarvest = entries.sumOf { it.litersHarvested.toDouble() }.toFloat()

    // Date pickers for custom range
    if (showCustomFrom) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showCustomFrom = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        customFrom = java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    }
                    showCustomFrom = false
                    showCustomTo = true
                }) { Text("Next") }
            },
            dismissButton = { TextButton(onClick = { showCustomFrom = false }) { Text("Cancel") } }
        ) { DatePicker(state = state) }
    }

    if (showCustomTo) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showCustomTo = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        customTo = java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    }
                    showCustomTo = false
                    userId?.let { uid -> viewModel.applyFilter(uid, "Custom Range", customFrom, customTo) }
                }) { Text("Apply") }
            },
            dismissButton = { TextButton(onClick = { showCustomTo = false }) { Text("Cancel") } }
        ) { DatePicker(state = state) }
    }

    // Delete confirmation dialog
    showDeleteDialog?.let { entry ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Entry") },
            text = { Text("Are you sure you want to delete this rainfall entry from ${entry.date}?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteEntry(entry)
                    showDeleteDialog = null
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) { Text("Cancel") }
            }
        )
    }

    // Edit Bottom Sheet
    if (showEditSheet != null) {
        EditEntrySheet(
            entry = showEditSheet!!,
            onDismiss = { showEditSheet = null },
            onUpdate = { updated ->
                viewModel.updateEntry(updated)
                showEditSheet = null
            }
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Filter Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = {
                        if (filter == "Custom Range") {
                            showCustomFrom = true
                        } else {
                            userId?.let { uid -> viewModel.applyFilter(uid, filter) }
                        }
                    },
                    label = { Text(filter, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Summary banner
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.1f))
        ) {
            Text(
                text = "${entries.size} entries | ${"%.1f".format(totalRainfall)} mm rainfall | ${"%.1f".format(totalHarvest)} L harvested",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(12.dp),
                color = Primary,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (entries.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("💧", style = MaterialTheme.typography.displayLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text("No rainfall logged yet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Start tracking your water harvest", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onNavigateToLogRainfall,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessWater)
                ) {
                    Text("Log Your First Entry", color = Color.White)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(entries, key = { it.id }) { entry ->
                    RainfallHistoryCard(
                        entry = entry,
                        onEdit = { showEditSheet = entry },
                        onDelete = { showDeleteDialog = entry }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditEntrySheet(
    entry: RainfallEntry,
    onDismiss: () -> Unit,
    onUpdate: (RainfallEntry) -> Unit
) {
    var rainfallMm by remember { mutableStateOf(entry.rainfallMm.toString()) }
    var source by remember { mutableStateOf(entry.source) }
    var notes by remember { mutableStateOf(entry.notes) }
    var sourceExpanded by remember { mutableStateOf(false) }

    val sources = listOf("Manual Measurement", "Weather App Reading", "Estimated")

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Edit Entry", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Date: ${entry.date}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = rainfallMm,
                onValueChange = { rainfallMm = it },
                label = { Text("Rainfall (mm)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, cursorColor = Primary),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(expanded = sourceExpanded, onExpandedChange = { sourceExpanded = !sourceExpanded }) {
                OutlinedTextField(
                    value = source,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Source") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sourceExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                )
                ExposedDropdownMenu(expanded = sourceExpanded, onDismissRequest = { sourceExpanded = false }) {
                    sources.forEach { s ->
                        DropdownMenuItem(text = { Text(s) }, onClick = { source = s; sourceExpanded = false })
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { if (it.length <= 200) notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, cursorColor = Primary),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        val mm = rainfallMm.toFloatOrNull() ?: entry.rainfallMm
                        val liters = entry.roofAreaUsed * mm * 0.0929f * entry.runoffUsed
                        onUpdate(entry.copy(rainfallMm = mm, source = source, notes = notes, litersHarvested = liters))
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Update", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
