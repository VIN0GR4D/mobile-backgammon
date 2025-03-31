package com.example.backgammon.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.backgammon.data.model.PlayerStatistics
import com.google.gson.Gson

class StatisticsManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREF_NAME = "backgammon_statistics"
        private const val KEY_STATISTICS = "player_statistics"
    }

    // Получение статистики
    fun getStatistics(): PlayerStatistics {
        val statisticsJson = sharedPreferences.getString(KEY_STATISTICS, null)
        return if (statisticsJson != null) {
            try {
                gson.fromJson(statisticsJson, PlayerStatistics::class.java)
            } catch (e: Exception) {
                PlayerStatistics()
            }
        } else {
            PlayerStatistics()
        }
    }

    // Сохранение статистики
    fun saveStatistics(statistics: PlayerStatistics) {
        val statisticsJson = gson.toJson(statistics)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_STATISTICS, statisticsJson)
        editor.apply()
    }

    // Обновление статистики после игры
    fun updateStatisticsAfterGame(playerWon: Boolean, movesCount: Int) {
        val currentStats = getStatistics()

        val totalGames = currentStats.totalGames + 1
        val wins = if (playerWon) currentStats.wins + 1 else currentStats.wins
        val losses = if (!playerWon) currentStats.losses + 1 else currentStats.losses
        val winRate = if (totalGames > 0) wins.toFloat() / totalGames else 0f

        // Расчет средних ходов на игру
        val totalMoves = (currentStats.averageMovesPerGame * currentStats.totalGames) + movesCount
        val averageMovesPerGame = if (totalGames > 0) totalMoves / totalGames else 0

        // Обновление серии побед
        val currentWinStreak = if (playerWon) currentStats.currentWinStreak + 1 else 0
        val longestWinStreak = maxOf(currentStats.longestWinStreak, currentWinStreak)

        val updatedStats = PlayerStatistics(
            totalGames = totalGames,
            wins = wins,
            losses = losses,
            winRate = winRate,
            averageMovesPerGame = averageMovesPerGame,
            longestWinStreak = longestWinStreak,
            currentWinStreak = currentWinStreak
        )

        saveStatistics(updatedStats)
    }

    // Сброс статистики
    fun resetStatistics() {
        saveStatistics(PlayerStatistics())
    }
}