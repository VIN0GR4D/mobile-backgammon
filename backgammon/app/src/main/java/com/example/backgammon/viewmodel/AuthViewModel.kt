package com.example.backgammon.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.backgammon.data.preferences.SessionManager
import com.example.backgammon.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val username: String = "",
    val error: String? = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository()
    private val sessionManager = SessionManager(application.applicationContext)

    private val _state = MutableStateFlow(
        AuthState(
            isLoggedIn = sessionManager.isLoggedIn(),
            username = sessionManager.getUsername() ?: ""
        )
    )
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        // Проверяем авторизацию при создании ViewModel
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        if (sessionManager.isLoggedIn()) {
            _state.update {
                it.copy(
                    isLoggedIn = true,
                    username = sessionManager.getUsername() ?: ""
                )
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            // Показываем индикатор загрузки
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val result = authRepository.login(username, password)

                result.fold(
                    onSuccess = { response ->
                        if (response.success) {
                            // Сохраняем данные авторизации
                            response.token?.let { token ->
                                sessionManager.saveAuthUser(token, response.username ?: username)
                            }

                            _state.update {
                                it.copy(
                                    isLoggedIn = true,
                                    username = response.username ?: username,
                                    isLoading = false
                                )
                            }
                        } else {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = response.message ?: "Ошибка авторизации"
                                )
                            }
                        }
                    },
                    onFailure = { e ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = e.message ?: "Ошибка соединения с сервером"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Неизвестная ошибка"
                    )
                }
            }
        }
    }

    fun register(username: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            // Показываем индикатор загрузки
            _state.update { it.copy(isLoading = true, error = null) }

            // Предварительная валидация данных
            val validationError = validateRegisterInput(username, email, password, confirmPassword)
            if (validationError != null) {
                _state.update { it.copy(isLoading = false, error = validationError) }
                return@launch
            }

            try {
                val result = authRepository.register(username, email, password)

                result.fold(
                    onSuccess = { response ->
                        if (response.success) {
                            // Сохраняем данные авторизации
                            response.token?.let { token ->
                                sessionManager.saveAuthUser(token, response.username ?: username)
                            }

                            _state.update {
                                it.copy(
                                    isLoggedIn = true,
                                    username = response.username ?: username,
                                    isLoading = false
                                )
                            }
                        } else {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = response.message ?: "Ошибка регистрации"
                                )
                            }
                        }
                    },
                    onFailure = { e ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = e.message ?: "Ошибка соединения с сервером"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Неизвестная ошибка"
                    )
                }
            }
        }
    }

    private fun validateRegisterInput(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): String? {
        return when {
            username.isEmpty() -> "Имя пользователя не может быть пустым"
            !email.contains("@") -> "Неверный формат email"
            password.length < 6 -> "Пароль должен содержать не менее 6 символов"
            password != confirmPassword -> "Пароли не совпадают"
            else -> null
        }
    }

    fun logout() {
        sessionManager.logout()
        _state.update {
            AuthState() // Сброс всех данных авторизации
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}