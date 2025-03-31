package com.example.backgammon.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.backgammon.viewmodel.AuthViewModel

@Composable
fun MainMenuScreen(
    authViewModel: AuthViewModel,
    onStartGame: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToStatistics: () -> Unit
) {
    val authState by authViewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Заголовок
        Text(
            text = "Нарды",
            fontSize = 40.sp,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // Приветствие (если пользователь авторизован)
        if (authState.isLoggedIn) {
            Text(
                text = "Добро пожаловать, ${authState.username}!",
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }

        // Кнопка начала игры
        Button(
            onClick = onStartGame,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = "Начать игру",
                fontSize = 18.sp
            )
        }

        // Кнопка статистики
        Button(
            onClick = onNavigateToStatistics,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = "Статистика",
                fontSize = 18.sp
            )
        }

        // Кнопка настроек
        Button(
            onClick = onNavigateToSettings,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = "Настройки",
                fontSize = 18.sp
            )
        }

        // Кнопка выхода из аккаунта (если пользователь авторизован)
        if (authState.isLoggedIn) {
            OutlinedButton(
                onClick = {
                    authViewModel.logout()
                    onNavigateToLogin()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                Text("Выйти из аккаунта")
            }
        }
    }
}