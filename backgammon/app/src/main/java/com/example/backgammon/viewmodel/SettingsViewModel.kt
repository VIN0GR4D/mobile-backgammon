package com.example.backgammon.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.backgammon.data.preferences.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

data class SettingsState(
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val darkTheme: Boolean = false
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsManager = SettingsManager(application.applicationContext)

    private val _settingsState = MutableStateFlow(
        SettingsState(
            soundEnabled = settingsManager.isSoundEnabled(),
            vibrationEnabled = settingsManager.isVibrationEnabled(),
            darkTheme = settingsManager.isDarkTheme()
        )
    )
    val settingsState: StateFlow<SettingsState> = _settingsState.asStateFlow()

    fun updateSoundEnabled(enabled: Boolean) {
        settingsManager.setSoundEnabled(enabled)
        _settingsState.update { it.copy(soundEnabled = enabled) }
    }

    fun updateVibrationEnabled(enabled: Boolean) {
        settingsManager.setVibrationEnabled(enabled)
        _settingsState.update { it.copy(vibrationEnabled = enabled) }
    }

    fun updateDarkTheme(enabled: Boolean) {
        settingsManager.setDarkTheme(enabled)
        _settingsState.update { it.copy(darkTheme = enabled) }
    }
}