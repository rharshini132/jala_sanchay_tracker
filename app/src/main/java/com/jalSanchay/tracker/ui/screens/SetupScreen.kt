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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jalSanchay.tracker.ui.theme.Accent
import com.jalSanchay.tracker.ui.theme.Primary
import com.jalSanchay.tracker.ui.theme.SuccessWater
import com.jalSanchay.tracker.viewmodel.SetupState
import com.jalSanchay.tracker.viewmodel.SetupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    onSetupComplete: () -> Unit,
    onSkip: () -> Unit,
    viewModel: SetupViewModel = hiltViewModel()
) {
    val setupState by viewModel.setupState.collectAsState()
    val userId by viewModel.userId.collectAsState()
    val existingSetup by viewModel.existingSetup.collectAsState()

    var roofArea by remember { mutableStateOf("") }
    var roofMaterial by remember { mutableStateOf("Concrete/Tile") }
    var runoffCoefficient by remember { mutableFloatStateOf(0.85f) }
    var tankCapacity by remember { mutableStateOf("") }
    var tankMaterial by remember { mutableStateOf("Plastic") }
    var currentLevel by remember { mutableStateOf("0") }

    var roofExpanded by remember { mutableStateOf(true) }
    var tankExpanded by remember { mutableStateOf(true) }
    var infoExpanded by remember { mutableStateOf(false) }
    var roofDropdownExpanded by remember { mutableStateOf(false) }
    var tankDropdownExpanded by remember { mutableStateOf(false) }

    var roofAreaError by remember { mutableStateOf("") }
    var tankCapacityError by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    val roofMaterials = mapOf(
        "Concrete/Tile" to 0.85f,
        "Metal/GI Sheet" to 0.90f,
        "Asbestos" to 0.80f,
        "Thatch/Grass" to 0.40f
    )
    val tankMaterials = listOf("Plastic", "Concrete", "Metal", "Underground")

    // Load existing setup if editing
    LaunchedEffect(userId) {
        userId?.let { viewModel.loadExistingSetup(it) }
    }

    LaunchedEffect(existingSetup) {
        existingSetup?.let { setup ->
            roofArea = setup.roofAreaM2.toInt().toString()
            roofMaterial = setup.roofMaterial
            runoffCoefficient = setup.runoffCoefficient
            tankCapacity = setup.tankCapacityLiters.toInt().toString()
            tankMaterial = setup.tankMaterial
            currentLevel = setup.currentWaterLevelLiters.toInt().toString()
        }
    }

    LaunchedEffect(setupState) {
        when (setupState) {
            is SetupState.Success -> {
                snackbarHostState.showSnackbar("Setup saved!")
                onSetupComplete()
            }
            is SetupState.Error -> {
                snackbarHostState.showSnackbar((setupState as SetupState.Error).message)
            }
            else -> {}
        }
    }

    // Live formula preview
    val previewLiters by remember {
        derivedStateOf {
            val area = roofArea.toFloatOrNull() ?: 0f
            area * 10f * 0.0929f * runoffCoefficient
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Setup Your Harvesting System",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold, color = Primary
                )
            )
            Text(
                text = "Enter your roof and tank details once",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Section 1: Roof Setup
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
                        Text("🏠 Roof Setup", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        IconButton(onClick = { roofExpanded = !roofExpanded }) {
                            Icon(if (roofExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, "Toggle")
                        }
                    }
                    AnimatedVisibility(visible = roofExpanded, enter = expandVertically(), exit = shrinkVertically()) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = roofArea,
                                onValueChange = { roofArea = it; roofAreaError = "" },
                                label = { Text("Roof Area (m²)") },
                                isError = roofAreaError.isNotEmpty(),
                                supportingText = if (roofAreaError.isNotEmpty()) { { Text(roofAreaError) } } else null,
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, cursorColor = Primary),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            ExposedDropdownMenuBox(expanded = roofDropdownExpanded, onExpandedChange = { roofDropdownExpanded = !roofDropdownExpanded }) {
                                OutlinedTextField(
                                    value = roofMaterial,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Roof Material") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roofDropdownExpanded) },
                                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                                )
                                ExposedDropdownMenu(expanded = roofDropdownExpanded, onDismissRequest = { roofDropdownExpanded = false }) {
                                    roofMaterials.forEach { (material, coeff) ->
                                        DropdownMenuItem(
                                            text = { Text("$material (Coeff: $coeff)") },
                                            onClick = {
                                                roofMaterial = material
                                                runoffCoefficient = coeff
                                                roofDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Runoff Coefficient: $runoffCoefficient (auto-set)",
                                style = MaterialTheme.typography.bodySmall,
                                color = SuccessWater
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { infoExpanded = !infoExpanded }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Info, contentDescription = "Info", tint = Accent, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("What is a runoff coefficient?", style = MaterialTheme.typography.labelSmall, color = Accent)
                            }
                            AnimatedVisibility(visible = infoExpanded, enter = expandVertically(), exit = shrinkVertically()) {
                                Text(
                                    text = "The runoff coefficient is the fraction of rainfall that becomes runoff. A value of 0.85 means 85% of rain hitting the roof flows into your collection system, while 15% is lost to evaporation and absorption.",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 28.dp, top = 4.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 2: Tank Setup
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
                        Text("🪣 Tank Setup", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        IconButton(onClick = { tankExpanded = !tankExpanded }) {
                            Icon(if (tankExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, "Toggle")
                        }
                    }
                    AnimatedVisibility(visible = tankExpanded, enter = expandVertically(), exit = shrinkVertically()) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = tankCapacity,
                                onValueChange = { tankCapacity = it; tankCapacityError = "" },
                                label = { Text("Tank Capacity (liters)") },
                                isError = tankCapacityError.isNotEmpty(),
                                supportingText = if (tankCapacityError.isNotEmpty()) { { Text(tankCapacityError) } } else null,
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, cursorColor = Primary),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            ExposedDropdownMenuBox(expanded = tankDropdownExpanded, onExpandedChange = { tankDropdownExpanded = !tankDropdownExpanded }) {
                                OutlinedTextField(
                                    value = tankMaterial,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Tank Material") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tankDropdownExpanded) },
                                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                                )
                                ExposedDropdownMenu(expanded = tankDropdownExpanded, onDismissRequest = { tankDropdownExpanded = false }) {
                                    tankMaterials.forEach { mat ->
                                        DropdownMenuItem(text = { Text(mat) }, onClick = { tankMaterial = mat; tankDropdownExpanded = false })
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = currentLevel,
                                onValueChange = { currentLevel = it },
                                label = { Text("Current Water Level (liters)") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, cursorColor = Primary),
                                singleLine = true
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 3: Formula Preview
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.1f)),
                border = BorderStroke(1.dp, Primary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📐 Formula Preview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Liters harvested = ${roofArea.ifBlank { "?" }} m² × Rainfall mm × 0.0929 × $runoffCoefficient",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "For 10mm rain: you'd collect ≈ ${"%.1f".format(previewLiters)} liters",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = SuccessWater
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    var valid = true
                    val area = roofArea.toFloatOrNull()
                    val cap = tankCapacity.toFloatOrNull()
                    if (area == null || area <= 0) { roofAreaError = "Enter valid roof area"; valid = false }
                    if (cap == null || cap <= 0) { tankCapacityError = "Enter valid tank capacity"; valid = false }
                    if (valid && userId != null) {
                        viewModel.saveSetup(
                            userId = userId!!,
                            roofArea = area!!,
                            roofMaterial = roofMaterial,
                            runoffCoefficient = runoffCoefficient,
                            tankCapacity = cap!!,
                            tankMaterial = tankMaterial,
                            currentWaterLevel = currentLevel.toFloatOrNull() ?: 0f
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                if (setupState is SetupState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Save Setup & Continue", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onSkip,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Skip for Now", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
