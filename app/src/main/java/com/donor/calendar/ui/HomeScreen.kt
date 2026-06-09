package com.donor.calendar.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.donor.calendar.data.DonorGender
import com.donor.calendar.domain.AvailabilityResult
import java.time.format.DateTimeFormatter
import java.util.Locale

private val DATE_FMT = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru"))

@Composable
fun HomeScreen(
    uiState: MainUiState,
    onGenderChange: (DonorGender) -> Unit
) {
    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Заголовок приложения
        item {
            Text(
                text = "Календарь донора",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        // Карточка пола
        item {
            GenderToggleCard(
                gender = uiState.gender,
                donationsCount = uiState.donations.size,
                onGenderChange = onGenderChange
            )
        }

        // Разделитель
        item {
            Text(
                text = "Доступность донаций",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        // Карточки для каждого типа
        items(uiState.statuses) { status ->
            DonationStatusCard(status = status)
        }

        // Подсказка снизу
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "ℹ️ Нажмите «Добавить», чтобы записать новую донацию. Расчёты обновятся автоматически.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
fun GenderToggleCard(
    gender: DonorGender,
    donationsCount: Int,
    onGenderChange: (DonorGender) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Профиль донора",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = gender.displayName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Всего донаций: $donationsCount",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    )
                }

                // Переключатель М / Ж
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DonorGender.values().forEach { g ->
                        val selected = gender == g
                        FilterChip(
                            selected = selected,
                            onClick = { onGenderChange(g) },
                            label = {
                                Text(
                                    text = if (g == DonorGender.MALE) "М" else "Ж",
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DonationStatusCard(status: DonationStatusUiState) {
    val isAvailable = status.result is AvailabilityResult.Available

    val containerColor by animateColorAsState(
        targetValue = if (isAvailable) Color(0xFFE8F5E9) else Color(0xFFFFF3F3),
        animationSpec = tween(durationMillis = 400),
        label = "cardColor"
    )
    val borderColor = if (isAvailable) Color(0xFF81C784) else Color(0xFFEF9A9A)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Большая иконка типа
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = borderColor.copy(alpha = 0.3f),
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = status.type.icon,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = status.type.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF212121)
                )
                Spacer(Modifier.height(4.dp))

                when (val r = status.result) {
                    is AvailabilityResult.Available -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color(0xFF4CAF50)
                            ) {
                                Text(
                                    text = " ✓ Доступно сейчас ",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    is AvailabilityResult.NotAvailable -> {
                        Text(
                            text = "Через ${r.daysLeft} дн.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFFC62828),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Доступно с ${r.availableFrom.format(DATE_FMT)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF757575)
                        )
                    }
                }
            }
        }
    }
}
