package com.example.backgammon.viewmodel

import android.app.Application
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.backgammon.core.Board
import com.example.backgammon.core.BoardListenerInterface
import com.example.backgammon.core.Color
import com.example.backgammon.core.PositionOnBoard
import com.example.backgammon.data.preferences.SettingsManager
import com.example.backgammon.data.preferences.StatisticsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameState(
    val positions: List<PositionOnBoard> = List(24) { PositionOnBoard() },
    val diceValues: List<Int> = emptyList(),
    val currentTurn: Color = Color.BLACK,
    val selectedPosition: Int = -1,
    val possibleMoves: List<Int> = emptyList(),
    val gameOver: Boolean = false,
    val winner: Color? = null,
    val movesCount: Int = 0
)

class GameViewModel(application: Application) : AndroidViewModel(application), BoardListenerInterface {

    private val board = Board(this)
    private val settingsManager = SettingsManager(application.applicationContext)
    private val statisticsManager = StatisticsManager(application.applicationContext) // Добавлен менеджер статистики
    private val vibrator = ContextCompat.getSystemService(application.applicationContext, Vibrator::class.java)

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    init {
        resetGame()
    }

    fun resetGame() {
        board.clearAllBoard()
        updateGameState(resetMoves = true)
    }

    private fun updateGameState(resetMoves: Boolean = false) {
        _gameState.update {
            it.copy(
                positions = board.listOfPositions.toList(),
                diceValues = board.turns,
                currentTurn = board.currentTurn,
                gameOver = board.gameOverCheck() != null,
                winner = board.gameOverCheck(),
                movesCount = if (resetMoves) 0 else it.movesCount
            )
        }

        // Проверка окончания игры и обновление статистики
        val currentState = _gameState.value
        if (currentState.gameOver && currentState.winner != null) {
            // Сохраняем статистику игры
            // Считаем, что игрок играет за BLACK
            val playerWon = currentState.winner == Color.BLACK
            statisticsManager.updateStatisticsAfterGame(playerWon, currentState.movesCount)
        }
    }

    fun selectPosition(position: Int) {
        if (board.listOfPositions[position].color != board.currentTurn) {
            return
        }

        val possibleMoves = board.possibleMoves(position)
        _gameState.update {
            it.copy(
                selectedPosition = position,
                possibleMoves = possibleMoves
            )
        }
    }

    fun makeMove(from: Int, to: Int) {
        board.makeMove(from, to)
        _gameState.update {
            it.copy(
                selectedPosition = -1,
                possibleMoves = emptyList(),
                movesCount = it.movesCount + 1  // Увеличиваем счетчик ходов
            )
        }
        updateGameState()

        // Применяем настройки вибрации
        if (settingsManager.isVibrationEnabled()) {
            vibrateDevice()
        }
    }

    private fun vibrateDevice() {
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(100)
            }
        }
    }

    fun throwOutFromBoard(position: Int) {
        if (board.possibleToThrow(position)) {
            board.throwOutFromTheBoard(position)
            updateGameState()
        }
    }

    fun updateTurns() {
        board.updateTurns()
        updateGameState()
    }

    override fun showDices(firstDice: Int, secondDice: Int) {
        // Этот метод вызывается из Board, когда бросаются кости
        updateGameState()
    }
}