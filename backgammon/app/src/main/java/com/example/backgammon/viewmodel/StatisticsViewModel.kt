package com.example.backgammon.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.backgammon.data.model.PlayerStatistics
import com.example.backgammon.data.preferences.SessionManager
import com.example.backgammon.data.preferences.StatisticsManager
import com.example.backgammon.data.repository.StatisticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {

    private val statisticsManager = StatisticsManager(application.applicationContext)
    private val sessionManager = SessionManager(application.applicationContext)
    private val statisticsRepository = StatisticsRepository(sessionManager)

    private val _statisticsState = MutableStateFlow(statisticsManager.getStatistics())
    val statisticsState: StateFlow<PlayerStatistics> = _statisticsState.asStateFlow()

    // Добавляем флаг синхронизации
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    // Добавляем флаг для отображения ошибок
    private val _syncError = MutableStateFlow<String?>(null)
    val syncError: StateFlow<String?> = _syncError.asStateFlow()

    init {
        refreshStatistics()
    }

    fun refreshStatistics() {
        _statisticsState.update { statisticsManager.getStatistics() }

        // Если пользователь авторизован, пытаемся синхронизировать с сервером
        if (sessionManager.isLoggedIn()) {
            syncWithServer()
        }
    }

    fun recordGameResult(playerWon: Boolean, movesCount: Int) {
        statisticsManager.updateStatisticsAfterGame(playerWon, movesCount)
        refreshStatistics()

        // Если пользователь авторизован, отправляем обновление на сервер
        if (sessionManager.isLoggedIn()) {
            syncWithServer()
        }
    }

    fun resetStatistics() {
        statisticsManager.resetStatistics()
        refreshStatistics()

        // Если пользователь авторизован, отправляем сброс на сервер
        if (sessionManager.isLoggedIn()) {
            syncWithServer()
        }
    }

    // Синхронизация статистики с сервером
    private fun syncWithServer() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncError.value = null

            try {
                // Сначала пытаемся получить статистику с сервера
                val serverStatisticsResult = statisticsRepository.getStatistics()

                if (serverStatisticsResult.isSuccess) {
                    val serverStats = serverStatisticsResult.getOrNull()
                    val localStats = statisticsManager.getStatistics()

                    // Выбираем наиболее актуальную статистику
                    val mergedStats = mergeStatistics(localStats, serverStats ?: localStats)

                    // Обновляем локальные данные
                    statisticsManager.saveStatistics(mergedStats)
                    _statisticsState.update { mergedStats }

                    // Отправляем обновленные данные на сервер
                    statisticsRepository.updateStatistics(mergedStats)
                } else {
                    // Если не удалось получить, просто отправляем текущие данные
                    val localStats = statisticsManager.getStatistics()
                    statisticsRepository.updateStatistics(localStats)
                }
            } catch (e: Exception) {
                _syncError.value = e.message ?: "Ошибка синхронизации"
            } finally {
                _isSyncing.value = false
            }
        }
    }

    // Слияние статистики с выбором наиболее актуальной
    private fun mergeStatistics(local: PlayerStatistics, server: PlayerStatistics): PlayerStatistics {
        return PlayerStatistics(
            totalGames = maxOf(local.totalGames, server.totalGames),
            wins = maxOf(local.wins, server.wins),
            losses = maxOf(local.losses, server.losses),
            winRate = if (maxOf(local.totalGames, server.totalGames) > 0) {
                maxOf(local.wins, server.wins).toFloat() / maxOf(local.totalGames, server.totalGames)
            } else 0f,
            averageMovesPerGame = if (maxOf(local.totalGames, server.totalGames) > 0) {
                // Выбираем наиболее актуальное среднее число ходов
                if (local.totalGames >= server.totalGames) local.averageMovesPerGame else server.averageMovesPerGame
            } else 0,
            longestWinStreak = maxOf(local.longestWinStreak, server.longestWinStreak),
            currentWinStreak = maxOf(local.currentWinStreak, server.currentWinStreak)
        )
    }

    // Очистка ошибки синхронизации
    fun clearSyncError() {
        _syncError.value = null
    }
}