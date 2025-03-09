package com.example.backgammon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.backgammon.core.Board
import com.example.backgammon.core.BoardListenerInterface
import com.example.backgammon.core.Color
import com.example.backgammon.core.PositionOnBoard
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
    val winner: Color? = null
)

class GameViewModel : ViewModel(), BoardListenerInterface {

    private val board = Board(this)

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    init {
        resetGame()
    }

    fun resetGame() {
        board.clearAllBoard()
        updateGameState()
    }

    private fun updateGameState() {
        _gameState.update {
            it.copy(
                positions = board.listOfPositions.toList(),
                diceValues = board.turns,
                currentTurn = board.currentTurn,
                gameOver = board.gameOverCheck() != null,
                winner = board.gameOverCheck()
            )
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
                possibleMoves = emptyList()
            )
        }
        updateGameState()
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