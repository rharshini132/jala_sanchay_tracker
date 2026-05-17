package com.jalSanchay.tracker.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jalSanchay.tracker.ui.theme.Accent
import com.jalSanchay.tracker.ui.theme.Primary
import com.jalSanchay.tracker.ui.theme.SuccessWater
import com.jalSanchay.tracker.viewmodel.Tip
import com.jalSanchay.tracker.viewmodel.TipsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipsScreen(
    onBack: () -> Unit,
    viewModel: TipsViewModel = hiltViewModel()
) {
    val tips by viewModel.tips.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val bookmarkedIds by viewModel.bookmarkedIds.collectAsState()
    val context = LocalContext.current

    val bookmarkedTips = remember(tips, bookmarkedIds) {
        tips.filter { bookmarkedIds.contains(it.id) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Harvesting Tips", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.search(it) },
                placeholder = { Text("Search tips...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Primary, cursorColor = Primary),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Category filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { viewModel.selectCategory(category) },
                        label = { Text(category, style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Bookmarked tips section
                if (bookmarkedTips.isNotEmpty()) {
                    item {
                        Text("⭐ Saved Tips", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Accent)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    items(bookmarkedTips, key = { "bm_${it.id}" }) { tip ->
                        TipCard(
                            tip = tip,
                            isBookmarked = true,
                            onToggleBookmark = { viewModel.toggleBookmark(tip.id) },
                            onShare = {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "${tip.title}\n\n${tip.detail}\n\n— via Jal-Sanchay Tracker 💧")
                                }
                                context.startActivity(Intent.createChooser(intent, "Share Tip"))
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }

                item {
                    Text("All Tips", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                }

                items(tips, key = { it.id }) { tip ->
                    TipCard(
                        tip = tip,
                        isBookmarked = bookmarkedIds.contains(tip.id),
                        onToggleBookmark = { viewModel.toggleBookmark(tip.id) },
                        onShare = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "${tip.title}\n\n${tip.detail}\n\n— via Jal-Sanchay Tracker 💧")
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Tip"))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TipCard(
    tip: Tip,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onShare: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(tip.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text(tip.summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), maxLines = 2)
                }
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Toggle"
                )
            }

            // Badges row
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = { },
                    label = { Text(tip.category, style = MaterialTheme.typography.labelSmall) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = Primary.copy(alpha = 0.1f), labelColor = Primary)
                )
                AssistChip(
                    onClick = { },
                    label = { Text(tip.difficulty, style = MaterialTheme.typography.labelSmall) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (tip.difficulty) {
                            "Easy" -> SuccessWater.copy(alpha = 0.1f)
                            "Medium" -> Accent.copy(alpha = 0.1f)
                            else -> com.jalSanchay.tracker.ui.theme.Warning.copy(alpha = 0.1f)
                        },
                        labelColor = when (tip.difficulty) {
                            "Easy" -> SuccessWater
                            "Medium" -> Accent
                            else -> com.jalSanchay.tracker.ui.theme.Warning
                        }
                    )
                )
                AssistChip(
                    onClick = { },
                    label = { Text(tip.timeEstimate, style = MaterialTheme.typography.labelSmall) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surface, labelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                )
            }

            AnimatedVisibility(visible = expanded, enter = expandVertically(), exit = shrinkVertically()) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(tip.detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = onToggleBookmark) {
                            Icon(
                                if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (isBookmarked) Accent else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                        IconButton(onClick = onShare) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = Primary)
                        }
                    }
                }
            }
        }
    }
}
