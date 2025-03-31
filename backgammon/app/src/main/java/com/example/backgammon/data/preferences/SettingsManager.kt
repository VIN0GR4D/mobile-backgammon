package com.example.backgammon.data.preferences

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "backgammon_settings"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_DARK_THEME = "dark_theme"
    }

    // Звук
    fun isSoundEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_SOUND_ENABLED, true)
    }

    fun setSoundEnabled(enabled: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_SOUND_ENABLED, enabled)
        editor.apply()
    }

    // Вибрация
    fun isVibrationEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_VIBRATION_ENABLED, true)
    }

    fun setVibrationEnabled(enabled: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_VIBRATION_ENABLED, enabled)
        editor.apply()
    }

    // Темная тема
    fun isDarkTheme(): Boolean {
        return sharedPreferences.getBoolean(KEY_DARK_THEME, false)
    }

    fun setDarkTheme(enabled: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_DARK_THEME, enabled)
        editor.apply()
    }
}