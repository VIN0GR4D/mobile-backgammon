package com.example.backgammon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.backgammon.ui.BackgammonNavigation
import com.example.backgammon.ui.theme.BackgammonTheme
import com.example.backgammon.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        setContent {
            val settingsState by settingsViewModel.settingsState.collectAsState()

            BackgammonTheme(
                darkTheme = settingsState.darkTheme
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BackgammonNavigation()
                }
            }
        }
    }
}