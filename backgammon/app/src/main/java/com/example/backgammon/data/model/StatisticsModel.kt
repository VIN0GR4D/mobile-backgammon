package com.example.backgammon.data.model

data class PlayerStatistics(
    val totalGames: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val winRate: Float = 0f,
    val averageMovesPerGame: Int = 0,
    val longestWinStreak: Int = 0,
    val currentWinStreak: Int = 0
)