package com.jalSanchay.tracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jalSanchay.tracker.data.model.RainfallEntry
import com.jalSanchay.tracker.ui.theme.Accent
import com.jalSanchay.tracker.ui.theme.Primary
import com.jalSanchay.tracker.ui.theme.SuccessWater
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun RainfallHistoryCard(
    entry: RainfallEntry,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    val formattedDate = try {
                        val date = LocalDate.parse(entry.date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                    } catch (e: Exception) {
                        entry.date
                    }
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(
                        onClick = { },
                        label = { Text("${entry.rainfallMm} mm") },
                        leadingIcon = {
                            Icon(Icons.Default.WaterDrop, contentDescription = null, modifier = Modifier.size(14.dp))
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Primary.copy(alpha = 0.15f),
                            labelColor = Primary
                        )
                    )
                    AssistChip(
                        onClick = { },
                        label = { Text("${"%.1f".format(entry.litersHarvested)} L") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = SuccessWater.copy(alpha = 0.15f),
                            labelColor = SuccessWater
                        )
                    )
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle details"
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Tank level mini bar
                    Text("Tank Level", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    LinearProgressIndicator(
                        progress = { (entry.tankLevelAfterLiters / 5000f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .padding(vertical = 2.dp),
                        color = Accent,
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                    Text(
                        text = "${"%.0f".format(entry.tankLevelAfterLiters)} L",
                        style = MaterialTheme.typography.labelSmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Source: ${entry.source}", style = MaterialTheme.typography.bodySmall)

                    if (entry.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Notes: ${entry.notes}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Formula: ${entry.roofAreaUsed} m² × ${entry.rainfallMm} mm × 0.0929 × ${entry.runoffUsed} = ${"%.1f".format(entry.litersHarvested)} L",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (onEdit != null || onDelete != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (onEdit != null) {
                                TextButton(onClick = onEdit) {
                                    Text("Edit Entry")
                                }
                            }
                            if (onDelete != null) {
                                TextButton(
                                    onClick = onDelete,
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
