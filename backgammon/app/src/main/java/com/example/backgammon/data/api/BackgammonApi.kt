package com.example.backgammon.data.api

import com.example.backgammon.data.model.AuthResponse
import com.example.backgammon.data.model.LoginRequest
import com.example.backgammon.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BackgammonApi {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<AuthResponse>
}