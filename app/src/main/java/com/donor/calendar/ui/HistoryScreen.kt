package com.donor.calendar.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.donor.calendar.data.DonationEntity
import com.donor.calendar.domain.DonationRules
import java.time.format.DateTimeFormatter
import java.util.Locale

private val HISTORY_DATE_FMT = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru"))

@Composable
fun HistoryScreen(
    donations: List<DonationEntity>,
    onDelete: (DonationEntity) -> Unit
) {
    if (donations.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("🩸", style = MaterialTheme.typography.displayLarge)
                Text(
                    text = "История пуста",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "Нажмите «Добавить», чтобы\nзаписать первую донацию",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "История донаций",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Всего записей: ${donations.size}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )
        }

        items(
            items = donations,
            key = { it.id }
        ) { donation ->
            DonationHistoryItem(
                donation = donation,
                onDelete = { onDelete(donation) }
            )
        }

        // Нижний отступ, чтобы FAB не перекрывал последний элемент
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationHistoryItem(
    donation: DonationEntity,
    onDelete: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(true) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                showConfirmDialog = true
            }
            // Всегда false — не даём автоматически скрыть элемент без подтверждения
            false
        }
    )

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Удалить запись?") },
            text = {
                Text(
                    "Донация «${donation.type.displayName}» от " +
                    "${DonationRules.epochDayToLocalDate(donation.date).format(HISTORY_DATE_FMT)} " +
                    "будет удалена. Расчёты обновятся автоматически."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    visible = false
                    onDelete()
                }) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    AnimatedVisibility(
        visible = visible,
        exit = shrinkVertically(animationSpec = androidx.compose.animation.core.tween(300)) +
               fadeOut(animationSpec = androidx.compose.animation.core.tween(300))
    ) {
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {
                // Красный фон при свайпе влево
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(end = 20.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Удалить",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "Удалить",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            enableDismissFromStartToEnd = false,
            enableDismissFromEndToStart = true
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Иконка с цветным фоном
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = donation.type.icon,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = donation.type.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = DonationRules
                                .epochDayToLocalDate(donation.date)
                                .format(HISTORY_DATE_FMT),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                        )
                    }

                    // Кнопка удаления (помимо свайпа)
                    IconButton(onClick = { showConfirmDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Удалить",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}
