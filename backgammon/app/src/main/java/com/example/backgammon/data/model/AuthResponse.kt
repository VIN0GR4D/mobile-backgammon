package com.example.backgammon.data.model

data class AuthResponse(
    val success: Boolean,
    val token: String?,
    val username: String?,
    val message: String?
)