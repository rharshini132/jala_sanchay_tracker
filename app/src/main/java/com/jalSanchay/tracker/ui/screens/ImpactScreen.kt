package com.jalSanchay.tracker.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jalSanchay.tracker.ui.components.ImpactMeter
import com.jalSanchay.tracker.ui.theme.*

@Composable
fun ImpactScreen(
    totalHarvest: Float,
    modifier: Modifier = Modifier
) {
    val dailyNeed = 135f // WHO standard
    val daysSupplied = if (dailyNeed > 0) totalHarvest / dailyNeed else 0f
    val peopleSupplied = (totalHarvest / (dailyNeed * 30)).toInt()
    val bottles = totalHarvest.toInt()
    val gardenArea = 10f // m²
    val gardenDays = if (gardenArea * 5 > 0) totalHarvest / (gardenArea * 5) else 0f
    val moneySaved = totalHarvest * 0.05f
    val co2Saved = totalHarvest * 0.298f / 1000f
    val treesEquivalent = co2Saved / 21f // avg tree absorbs 21kg CO2/year

    data class ImpactCard(val title: String, val value: String, val detail: String, val emoji: String)

    val cards = listOf(
        ImpactCard("People Supplied", "Supply ${maxOf(1, peopleSupplied)} person(s) for ${"%.0f".format(daysSupplied)} days",
            "Based on WHO standard of 135 liters/person/day. Your total harvest: ${"%.0f".format(totalHarvest)} L", "👥"),
        ImpactCard("Drinking Water", "Equivalent to $bottles bottles (1L each)",
            "Your harvest equals $bottles one-liter drinking water bottles", "🍶"),
        ImpactCard("Garden Watering", "Water ${gardenArea.toInt()} m² garden for ${"%.0f".format(gardenDays)} days",
            "Based on 5 liters/m²/day watering requirement", "🌱"),
        ImpactCard("Money Saved", "Estimated ₹${"%.2f".format(moneySaved)} saved",
            "Water rate used: ₹0.05/liter. Edit in Settings", "💰"),
        ImpactCard("CO₂ Saved", "Equivalent to planting ${"%.1f".format(treesEquivalent)} trees",
            "Based on 0.298 kg CO₂ per 1000 liters of water treatment saved. Each tree absorbs ~21 kg CO₂/year", "🌍")
    )

    val expandedStates = remember { mutableStateMapOf<Int, Boolean>() }

    // Achievement badges
    data class Badge(val emoji: String, val title: String, val requirement: String, val earned: Boolean)

    val badges = listOf(
        Badge("💧", "First Drop", "Log first entry", totalHarvest > 0),
        Badge("🌊", "100 Liters", "Harvest 100 liters total", totalHarvest >= 100),
        Badge("🏆", "1000 Liters", "Harvest 1000 liters", totalHarvest >= 1000),
        Badge("🌍", "Eco Warrior", "30 consecutive days logged", false),
        Badge("⭐", "Water Wise", "Complete profile + setup", totalHarvest > 0)
    )

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
    ) {
        // 1. Impact Score Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(listOf(Secondary, Primary, Accent)),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ImpactMeter(
                            value = daysSupplied,
                            maxValue = 365f,
                            label = "Days of Water Supplied",
                            unit = "days"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Based on your total harvest of ${"%.0f".format(totalHarvest)} liters",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // 2. Impact Equivalents
        item {
            Text("Your Impact", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }

        items(cards.size) { index ->
            val card = cards[index]
            val expanded = expandedStates[index] ?: false
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedStates[index] = !expanded },
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
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Text(card.emoji, style = MaterialTheme.typography.headlineSmall)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(card.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Text(card.value, style = MaterialTheme.typography.bodySmall, color = SuccessWater)
                            }
                        }
                        Icon(
                            if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle"
                        )
                    }
                    AnimatedVisibility(visible = expanded, enter = expandVertically(), exit = shrinkVertically()) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(card.detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        }
                    }
                }
            }
        }

        // 4. Achievement Badges
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Achievement Badges", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(badges) { badge ->
                    var showReq by remember { mutableStateOf(false) }
                    Card(
                        modifier = Modifier
                            .width(120.dp)
                            .clickable { showReq = !showReq },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (badge.earned) SuccessWater.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
                        ),
                        border = BorderStroke(1.dp, if (badge.earned) SuccessWater.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                badge.emoji,
                                style = MaterialTheme.typography.headlineMedium,
                                color = if (badge.earned) Color.Unspecified else Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                badge.title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = if (badge.earned) MaterialTheme.colorScheme.onSurface else Color.Gray
                            )
                            AnimatedVisibility(visible = showReq) {
                                Text(
                                    if (badge.earned) "✅ Earned!" else badge.requirement,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (badge.earned) SuccessWater else Color.Gray,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
