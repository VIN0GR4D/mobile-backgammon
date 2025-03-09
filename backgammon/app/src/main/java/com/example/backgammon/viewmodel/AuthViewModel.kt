package com.example.backgammon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
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

class AuthViewModel : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            // Показываем индикатор загрузки
            _state.update { it.copy(isLoading = true, error = null) }

            // Имитация задержки сетевого запроса
            delay(1500)

            // Проверка учетных данных (временная реализация)
            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Успешный вход
                _state.update {
                    it.copy(
                        isLoggedIn = true,
                        username = username,
                        isLoading = false
                    )
                }
            } else {
                // Ошибка входа
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Неверное имя пользователя или пароль"
                    )
                }
            }
        }
    }

    fun register(username: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            // Показываем индикатор загрузки
            _state.update { it.copy(isLoading = true, error = null) }

            // Имитация задержки сетевого запроса
            delay(2000)

            // Проверка данных
            when {
                username.isEmpty() -> {
                    _state.update { it.copy(isLoading = false, error = "Имя пользователя не может быть пустым") }
                }
                !email.contains("@") -> {
                    _state.update { it.copy(isLoading = false, error = "Неверный формат email") }
                }
                password.length < 6 -> {
                    _state.update { it.copy(isLoading = false, error = "Пароль должен содержать не менее 6 символов") }
                }
                password != confirmPassword -> {
                    _state.update { it.copy(isLoading = false, error = "Пароли не совпадают") }
                }
                else -> {
                    // Успешная регистрация
                    _state.update {
                        it.copy(
                            isLoggedIn = true,
                            username = username,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun logout() {
        _state.update {
            AuthState() // Сброс всех данных авторизации
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}