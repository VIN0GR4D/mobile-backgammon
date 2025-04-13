package com.example.backgammon.ui.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.backgammon.viewmodel.StatisticsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    statisticsViewModel: StatisticsViewModel,
    onNavigateBack: () -> Unit
) {
    val statistics by statisticsViewModel.statisticsState.collectAsState()

    var showResetDialog by remember { mutableStateOf(false) }

    // Добавляем индикатор загрузки и сообщение об ошибке
    val isSyncing by statisticsViewModel.isSyncing.collectAsState()
    val syncError by statisticsViewModel.syncError.collectAsState()

    if (isSyncing) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }

    syncError?.let { error ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { statisticsViewModel.clearSyncError() }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Закрыть",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }

    // Диалог подтверждения сброса статистики
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Сбросить статистику?") },
            text = { Text("Вы уверены, что хотите сбросить всю игровую статистику? Это действие нельзя отменить.") },
            confirmButton = {
                Button(
                    onClick = {
                        statisticsViewModel.resetStatistics()
                        showResetDialog = false
                    }
                ) {
                    Text("Сбросить")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showResetDialog = false }
                ) {
                    Text("Отмена")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Заголовок и кнопка сброса
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Статистика игр",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = { showResetDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Сбросить статистику"
                )
            }
        }

        // Статистические карточки
        StatisticCard(
            title = "Общая статистика",
            stats = listOf(
                "Всего игр" to statistics.totalGames.toString(),
                "Побед" to statistics.wins.toString(),
                "Поражений" to statistics.losses.toString(),
                "Процент побед" to String.format("%.1f%%", statistics.winRate * 100)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        StatisticCard(
            title = "Подробная статистика",
            stats = listOf(
                "Ср. ходов за игру" to statistics.averageMovesPerGame.toString(),
                "Серия побед" to statistics.currentWinStreak.toString(),
                "Макс. серия побед" to statistics.longestWinStreak.toString()
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        // Кнопка назад
        Button(
            onClick = onNavigateBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Назад")
        }
    }
}

@Composable
fun StatisticCard(
    title: String,
    stats: List<Pair<String, String>>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            stats.forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = label, fontSize = 16.sp)
                    Text(
                        text = value,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}