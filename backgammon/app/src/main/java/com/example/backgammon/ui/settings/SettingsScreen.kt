package com.example.backgammon.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.backgammon.data.preferences.SettingsManager
import com.example.backgammon.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val settingsState by settingsViewModel.settingsState.collectAsState()
    var soundEnabled by remember { mutableStateOf(settingsState.soundEnabled) }
    var vibrationEnabled by remember { mutableStateOf(settingsState.vibrationEnabled) }
    var darkTheme by remember { mutableStateOf(settingsState.darkTheme) }

    // Обновляем локальные состояния при изменении настроек
    LaunchedEffect(settingsState) {
        soundEnabled = settingsState.soundEnabled
        vibrationEnabled = settingsState.vibrationEnabled
        darkTheme = settingsState.darkTheme
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Заголовок
        Text(
            text = "Настройки",
            fontSize = 28.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Звук
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Звук",
                fontSize = 18.sp
            )
            Switch(
                checked = soundEnabled,
                onCheckedChange = {
                    soundEnabled = it
                    settingsViewModel.updateSoundEnabled(it)
                }
            )
        }

        // Вибрация
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Вибрация",
                fontSize = 18.sp
            )
            Switch(
                checked = vibrationEnabled,
                onCheckedChange = {
                    vibrationEnabled = it
                    settingsViewModel.updateVibrationEnabled(it)
                }
            )
        }

        // Темная тема
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Темная тема",
                fontSize = 18.sp
            )
            Switch(
                checked = darkTheme,
                onCheckedChange = {
                    darkTheme = it
                    settingsViewModel.updateDarkTheme(it)
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Кнопка назад
        Button(
            onClick = onNavigateBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Назад")
        }
    }
}