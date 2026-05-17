package com.jalSanchay.tracker.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jalSanchay.tracker.ui.theme.Accent
import com.jalSanchay.tracker.ui.theme.Primary
import com.jalSanchay.tracker.ui.theme.SuccessWater
import com.jalSanchay.tracker.ui.theme.Warning
import com.jalSanchay.tracker.viewmodel.LogRainfallState
import com.jalSanchay.tracker.viewmodel.LogRainfallViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogRainfallScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LogRainfallViewModel = hiltViewModel()
) {
    val logState by viewModel.logState.collectAsState()
    val tankSetup by viewModel.tankSetup.collectAsState()
    val userId by viewModel.userId.collectAsState()

    var date by remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) }
    var rainfallMm by remember { mutableStateOf("") }
    var source by remember { mutableStateOf("Manual Measurement") }
    var notes by remember { mutableStateOf("") }
    var updateTank by remember { mutableStateOf(true) }
    var sourceDropdownExpanded by remember { mutableStateOf(false) }
    var tankUpdateExpanded by remember { mutableStateOf(true) }
    var formulaExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var rainfallError by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val sources = listOf("Manual Measurement", "Weather App Reading", "Estimated")

    LaunchedEffect(userId) {
        userId?.let { viewModel.loadSetup(it) }
    }

    // Live preview
    val previewLiters by remember {
        derivedStateOf {
            val mm = rainfallMm.toFloatOrNull() ?: 0f
            viewModel.calculatePreview(mm)
        }
    }

    val newTankLevel by remember {
        derivedStateOf {
            val setup = tankSetup
            if (setup != null) {
                minOf(setup.currentWaterLevelLiters + previewLiters, setup.tankCapacityLiters)
            } else 0f
        }
    }

    val isOverflow by remember {
        derivedStateOf {
            val setup = tankSetup
            if (setup != null) {
                (setup.currentWaterLevelLiters + previewLiters) > setup.tankCapacityLiters
            } else false
        }
    }

    LaunchedEffect(logState) {
        when (logState) {
            is LogRainfallState.Success -> {
                val liters = (logState as LogRainfallState.Success).litersHarvested
                snackbarHostState.showSnackbar("Entry saved! You harvested ${"%.1f".format(liters)} liters")
                viewModel.resetState()
                onSaved()
            }
            is LogRainfallState.Error -> {
                snackbarHostState.showSnackbar((logState as LogRainfallState.Error).message)
            }
            else -> {}
        }
    }

    // Date picker dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        date = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text("Log Rainfall Entry", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Primary)
            Spacer(modifier = Modifier.height(16.dp))

            // Date picker
            OutlinedTextField(
                value = date,
                onValueChange = {},
                readOnly = true,
                label = { Text("Date") },
                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.EditCalendar, contentDescription = "Pick date")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Section 1: Rainfall Input
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🌧 Rainfall Input", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = rainfallMm,
                        onValueChange = { rainfallMm = it; rainfallError = "" },
                        label = { Text("Rainfall (mm)") },
                        isError = rainfallError.isNotEmpty(),
                        supportingText = if (rainfallError.isNotEmpty()) { { Text(rainfallError) } } else null,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, cursorColor = Primary),
                        singleLine = true
                    )

                    if (previewLiters > 0) {
                        Text(
                            text = "≈ ${"%.1f".format(previewLiters)} liters will be harvested",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SuccessWater,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ExposedDropdownMenuBox(expanded = sourceDropdownExpanded, onExpandedChange = { sourceDropdownExpanded = !sourceDropdownExpanded }) {
                        OutlinedTextField(
                            value = source,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Source") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sourceDropdownExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                        )
                        ExposedDropdownMenu(expanded = sourceDropdownExpanded, onDismissRequest = { sourceDropdownExpanded = false }) {
                            sources.forEach { s ->
                                DropdownMenuItem(text = { Text(s) }, onClick = { source = s; sourceDropdownExpanded = false })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { if (it.length <= 200) notes = it },
                        label = { Text("Notes (optional)") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, cursorColor = Primary),
                        maxLines = 4
                    )
                    Text(
                        text = "${notes.length}/200",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 2: Tank Update
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Update tank level?", style = MaterialTheme.typography.titleSmall)
                        Switch(
                            checked = updateTank,
                            onCheckedChange = { updateTank = it; tankUpdateExpanded = it },
                            colors = SwitchDefaults.colors(checkedTrackColor = Primary)
                        )
                    }

                    AnimatedVisibility(visible = updateTank && tankSetup != null, enter = expandVertically(), exit = shrinkVertically()) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            val tankCap = tankSetup?.tankCapacityLiters ?: 1f
                            val pct = (newTankLevel / tankCap * 100).coerceIn(0f, 100f)
                            Text("New tank level: ${"%.0f".format(newTankLevel)} L (${"%.0f".format(pct)}% full)", style = MaterialTheme.typography.bodyMedium, color = Accent)

                            if (isOverflow) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "⚠ Overflow! Tank capacity is ${"%.0f".format(tankSetup?.tankCapacityLiters)} liters",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Warning,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 3: Formula Breakdown
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.08f)),
                border = BorderStroke(1.dp, Primary.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📐 See Calculation", style = MaterialTheme.typography.titleSmall, color = Primary)
                        IconButton(onClick = { formulaExpanded = !formulaExpanded }) {
                            Icon(if (formulaExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, "Toggle", tint = Primary)
                        }
                    }
                    AnimatedVisibility(visible = formulaExpanded, enter = expandVertically(), exit = shrinkVertically()) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            val setup = tankSetup
                            Text("Roof Area: ${setup?.roofAreaM2 ?: "N/A"} m²", style = MaterialTheme.typography.bodySmall)
                            Text("Rainfall: ${rainfallMm.ifBlank { "0" }} mm", style = MaterialTheme.typography.bodySmall)
                            Text("Runoff Coefficient: ${setup?.runoffCoefficient ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
                            Text("Conversion Factor: 0.0929", style = MaterialTheme.typography.bodySmall)
                            Divider(modifier = Modifier.padding(vertical = 4.dp))
                            Text("= ${"%.1f".format(previewLiters)} liters", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = SuccessWater)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val mm = rainfallMm.toFloatOrNull()
                    if (mm == null || mm <= 0) {
                        rainfallError = "Enter valid rainfall amount"
                        return@Button
                    }
                    userId?.let { uid ->
                        viewModel.saveEntry(uid, date, mm, source, notes, updateTank)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                if (logState is LogRainfallState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Save Entry", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
