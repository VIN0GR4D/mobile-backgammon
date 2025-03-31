package com.example.backgammon.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.backgammon.data.model.PlayerStatistics
import com.example.backgammon.data.preferences.StatisticsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {

    private val statisticsManager = StatisticsManager(application.applicationContext)

    private val _statisticsState = MutableStateFlow(statisticsManager.getStatistics())
    val statisticsState: StateFlow<PlayerStatistics> = _statisticsState.asStateFlow()

    init {
        refreshStatistics()
    }

    fun refreshStatistics() {
        _statisticsState.update { statisticsManager.getStatistics() }
    }

    fun recordGameResult(playerWon: Boolean, movesCount: Int) {
        statisticsManager.updateStatisticsAfterGame(playerWon, movesCount)
        refreshStatistics()
    }

    fun resetStatistics() {
        statisticsManager.resetStatistics()
        refreshStatistics()
    }
}