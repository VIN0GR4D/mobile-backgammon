package com.example.backgammon.data.api

import com.example.backgammon.data.model.AuthResponse
import com.example.backgammon.data.model.LoginRequest
import com.example.backgammon.data.model.PlayerStatistics
import com.example.backgammon.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface BackgammonApi {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<AuthResponse>

    @GET("api/statistics")
    suspend fun getStatistics(@Header("Authorization") token: String): Response<PlayerStatistics>

    @POST("api/statistics")
    suspend fun updateStatistics(
        @Header("Authorization") token: String,
        @Body statistics: PlayerStatistics
    ): Response<PlayerStatistics>
}