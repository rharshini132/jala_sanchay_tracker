package com.jalSanchay.tracker.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jalSanchay.tracker.ui.theme.*
import com.jalSanchay.tracker.viewmodel.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onNavigateToSetup: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val userId by viewModel.userId.collectAsState()
    val user by viewModel.user.collectAsState()
    val tankSetup by viewModel.tankSetup.collectAsState()
    val totalHarvest by viewModel.totalHarvest.collectAsState()
    val totalRainfall by viewModel.totalRainfall.collectAsState()
    val entryCount by viewModel.entryCount.collectAsState()
    val dailyNeed by viewModel.dailyNeed.collectAsState()
    val context = LocalContext.current

    var isEditing by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editCity by remember { mutableStateOf("") }
    var editHousehold by remember { mutableStateOf("") }
    var dangerExpanded by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        userId?.let { viewModel.loadProfile(it) }
    }

    LaunchedEffect(user) {
        user?.let {
            editName = it.name
            editCity = it.city
            editHousehold = it.householdSize
        }
    }

    // Dialogs
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear All History") },
            text = { Text("This will delete all rainfall entries. Setup will be kept.") },
            confirmButton = {
                TextButton(onClick = {
                    userId?.let { viewModel.clearAllHistory(it) }
                    showClearDialog = false
                }) { Text("Clear", color = Error) }
            },
            dismissButton = { TextButton(onClick = { showClearDialog = false }) { Text("Cancel") } }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account") },
            text = { Text("This will permanently delete your account and all data. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    userId?.let { viewModel.deleteAccount(it) }
                    showDeleteDialog = false
                    onLogout()
                }) { Text("Delete", color = Error) }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Avatar
            val initials = user?.name?.split(" ")?.mapNotNull { it.firstOrNull()?.uppercase() }?.take(2)?.joinToString("") ?: "?"
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Secondary, Primary, Accent))),
                contentAlignment = Alignment.Center
            ) {
                Text(initials, style = MaterialTheme.typography.headlineMedium.copy(color = Color.White, fontWeight = FontWeight.Bold))
            }

            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { Toast.makeText(context, "Photo upload coming soon", Toast.LENGTH_SHORT).show() }) {
                Text("Change Photo", color = Accent, style = MaterialTheme.typography.labelSmall)
            }

            if (isEditing) {
                // Edit mode
                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, cursorColor = Primary),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = editCity,
                    onValueChange = { editCity = it },
                    label = { Text("City") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, cursorColor = Primary),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                var householdExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = householdExpanded, onExpandedChange = { householdExpanded = !householdExpanded }) {
                    OutlinedTextField(
                        value = editHousehold,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Household Size") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = householdExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary)
                    )
                    ExposedDropdownMenu(expanded = householdExpanded, onDismissRequest = { householdExpanded = false }) {
                        listOf("1-2 members", "3-4 members", "5+ members").forEach { opt ->
                            DropdownMenuItem(text = { Text(opt) }, onClick = { editHousehold = opt; householdExpanded = false })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = {
                        isEditing = false
                        user?.let { editName = it.name; editCity = it.city; editHousehold = it.householdSize }
                    }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                        Text("Cancel")
                    }
                    Button(onClick = {
                        userId?.let { viewModel.updateProfile(it, editName, editCity, editHousehold) }
                        isEditing = false
                    }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                        Text("Save Changes", color = Color.White)
                    }
                }
            } else {
                // View mode
                Text(user?.name ?: "", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onBackground)
                Text(user?.city ?: "", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                user?.createdAt?.let {
                    val dateStr = SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date(it))
                    Text("Member since $dateStr", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f))
                }
                AssistChip(
                    onClick = {},
                    label = { Text(user?.householdSize ?: "", style = MaterialTheme.typography.labelSmall) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = Primary.copy(alpha = 0.1f), labelColor = Primary),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { isEditing = true }, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Profile", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // My Stats Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📊 My Stats", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))
                    StatRow("Total Rainfall Logged", "${"%.1f".format(totalRainfall)} mm")
                    StatRow("Total Water Harvested", "${"%.1f".format(totalHarvest)} L")
                    StatRow("Total Entries", "$entryCount")
                    StatRow("Impact Score", "${"%.1f".format(if (dailyNeed > 0) totalHarvest / dailyNeed else 0f)} days")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Setup Summary Card
            if (tankSetup != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🏠 Setup Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(12.dp))
                        StatRow("Roof Area", "${tankSetup!!.roofAreaM2} m²")
                        StatRow("Roof Material", tankSetup!!.roofMaterial)
                        StatRow("Tank Capacity", "${tankSetup!!.tankCapacityLiters} L")
                        StatRow("Runoff Coefficient", "${tankSetup!!.runoffCoefficient}")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(onClick = onNavigateToSetup, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                            Text("Edit Setup")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Danger Zone
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Error.copy(alpha = 0.05f)),
                border = BorderStroke(1.dp, Error.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⚠ Danger Zone", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Error)
                        IconButton(onClick = { dangerExpanded = !dangerExpanded }) {
                            Icon(if (dangerExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, "Toggle", tint = Error)
                        }
                    }
                    AnimatedVisibility(visible = dangerExpanded, enter = expandVertically(), exit = shrinkVertically()) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            OutlinedButton(
                                onClick = { showClearDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                border = ButtonDefaults.outlinedButtonBorder,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Error)
                            ) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Clear All History")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { showDeleteDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Error)
                            ) {
                                Icon(Icons.Default.PersonRemove, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Delete Account", color = Color.White)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}
