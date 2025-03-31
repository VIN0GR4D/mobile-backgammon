package com.example.backgammon.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.backgammon.ui.auth.LoginScreen
import com.example.backgammon.ui.auth.RegisterScreen
import com.example.backgammon.ui.game.GameScreen
import com.example.backgammon.ui.settings.SettingsScreen
import com.example.backgammon.ui.statistics.StatisticsScreen
import com.example.backgammon.viewmodel.AuthViewModel
import com.example.backgammon.viewmodel.GameViewModel
import com.example.backgammon.viewmodel.SettingsViewModel
import com.example.backgammon.viewmodel.StatisticsViewModel

@Composable
fun BackgammonNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val gameViewModel: GameViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()
    val statisticsViewModel: StatisticsViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToMainMenu = {
                    navController.navigate("main_menu") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("register") {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToMainMenu = {
                    navController.navigate("main_menu") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("main_menu") {
            MainMenuScreen(
                authViewModel = authViewModel,
                onStartGame = {
                    navController.navigate("game")
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("main_menu") { inclusive = true }
                    }
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
                },
                onNavigateToStatistics = {
                    navController.navigate("statistics")
                }
            )
        }

        composable("game") {
            GameScreen(
                gameViewModel = gameViewModel,
                onNavigateToMainMenu = {
                    navController.navigate("main_menu") {
                        popUpTo("game") { inclusive = true }
                    }
                }
            )
        }

        composable("settings") {
            SettingsScreen(
                settingsViewModel = settingsViewModel,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        composable("statistics") {
            StatisticsScreen(
                statisticsViewModel = statisticsViewModel,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}