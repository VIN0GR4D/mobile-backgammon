package com.example.backgammon.data.repository

import com.example.backgammon.data.api.RetrofitClient
import com.example.backgammon.data.model.PlayerStatistics
import com.example.backgammon.data.preferences.SessionManager

class StatisticsRepository(private val sessionManager: SessionManager) {
    private val api = RetrofitClient.api

    suspend fun getStatistics(): Result<PlayerStatistics> {
        val token = sessionManager.getToken() ?: return Result.failure(Exception("Не авторизован"))

        return try {
            val response = api.getStatistics("Bearer $token")
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Пустой ответ"))
            } else {
                Result.failure(Exception("Ошибка: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStatistics(statistics: PlayerStatistics): Result<PlayerStatistics> {
        val token = sessionManager.getToken() ?: return Result.failure(Exception("Не авторизован"))

        return try {
            val response = api.updateStatistics("Bearer $token", statistics)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Пустой ответ"))
            } else {
                Result.failure(Exception("Ошибка: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}