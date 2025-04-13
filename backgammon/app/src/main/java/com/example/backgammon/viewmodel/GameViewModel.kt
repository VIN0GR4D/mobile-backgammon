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
import kotlinx.coroutines.delay
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
    val movesCount: Int = 0,
    val isRollingDice: Boolean = false,  // Новое поле для анимации
    val hints: List<Pair<Int, Int>> = emptyList() // Подсказки в формате <откуда, куда>
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
                possibleMoves = possibleMoves,
                hints = emptyList() // Скрываем подсказки при выборе
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
        if (board.turns.isEmpty()) {
            rollDice()
        }
    }

    // Функция для анимации броска костей
    fun rollDice() {
        if (_gameState.value.isRollingDice || _gameState.value.diceValues.isNotEmpty()) {
            return // Предотвращаем повторный бросок, если анимация уже идёт или кости уже брошены
        }

        viewModelScope.launch {
            // Устанавливаем флаг анимации
            _gameState.update { it.copy(isRollingDice = true) }

            // Анимируем бросок
            val animationDuration = 800L
            val fps = 8
            val interval = animationDuration / fps

            repeat(fps) {
                // Генерируем случайные значения для визуальной анимации
                _gameState.update {
                    it.copy(diceValues = listOf((1..6).random(), (1..6).random()))
                }
                delay(interval)
            }

            // Завершаем анимацию, обновляем ходы
            board.updateTurns()
            _gameState.update {
                it.copy(
                    diceValues = board.turns,
                    isRollingDice = false
                )
            }
        }
    }

    // Функция для отображения подсказок
    fun showHints() {
        if (_gameState.value.selectedPosition != -1) {
            // Если уже выбрана позиция, не показываем подсказки
            return
        }

        val possiblePositions = mutableListOf<Int>()

        // Находим все позиции с шашками текущего игрока
        for (i in 0 until 24) {
            if (board.listOfPositions[i].color == board.currentTurn) {
                possiblePositions.add(i)
            }
        }

        // Находим все возможные ходы
        val allPossibleMoves = mutableListOf<Pair<Int, Int>>()
        possiblePositions.forEach { pos ->
            val moves = board.possibleMoves(pos)
            moves.forEach { move ->
                allPossibleMoves.add(Pair(pos, move))
            }
        }

        _gameState.update {
            it.copy(hints = allPossibleMoves)
        }
    }

    // Функция для скрытия подсказок
    fun hideHints() {
        _gameState.update {
            it.copy(hints = emptyList())
        }
    }

    override fun showDices(firstDice: Int, secondDice: Int) {
        // Этот метод вызывается из Board, когда бросаются кости
        updateGameState()
    }
}