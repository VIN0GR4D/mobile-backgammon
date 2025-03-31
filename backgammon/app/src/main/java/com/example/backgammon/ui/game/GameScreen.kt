package com.example.backgammon.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.backgammon.core.Color
import com.example.backgammon.core.PositionOnBoard
import com.example.backgammon.viewmodel.GameViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun GameScreen(
    gameViewModel: GameViewModel = viewModel(),
    onNavigateToMainMenu: () -> Unit
) {
    val gameState by gameViewModel.gameState.collectAsState()

    // Отображение диалога завершения игры, если игра окончена
    if (gameState.gameOver) {
        GameOverDialog(
            winner = gameState.winner,
            onPlayAgain = { gameViewModel.resetGame() },
            onNavigateToMainMenu = onNavigateToMainMenu
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Информация о текущем ходе
        Text(
            text = "Ход: ${if (gameState.currentTurn == Color.BLACK) "Черные" else "Белые"}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Отображение количества ходов
        Text(
            text = "Количество ходов: ${gameState.movesCount}",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Отображение костей
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            gameState.diceValues.forEach { diceValue ->
                DiceView(value = diceValue)
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        // Игровое поле
        BackgammonBoard(
            positions = gameState.positions,
            selectedPosition = gameState.selectedPosition,
            possibleMoves = gameState.possibleMoves,
            onPositionClick = { position ->
                val selected = gameState.selectedPosition

                if (selected != -1 && gameState.possibleMoves.contains(position)) {
                    // Если уже выбрана позиция и это допустимый ход
                    gameViewModel.makeMove(selected, position)
                } else {
                    // Выбираем новую позицию
                    gameViewModel.selectPosition(position)
                }
            }
        )

        // Кнопки управления
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { gameViewModel.updateTurns() }) {
                Text("Завершить ход")
            }

            Button(onClick = { gameViewModel.resetGame() }) {
                Text("Новая игра")
            }

            Button(onClick = onNavigateToMainMenu) {
                Text("Главное меню")
            }
        }
    }
}

@Composable
fun DiceView(value: Int) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(ComposeColor.White)
            .border(1.dp, ComposeColor.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BackgammonBoard(
    positions: List<PositionOnBoard>,
    selectedPosition: Int,
    possibleMoves: List<Int>,
    onPositionClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(ComposeColor(0xFF8B4513)) // Цвет доски - коричневый
            .padding(4.dp)
    ) {
        // Верхняя часть доски (позиции 12-23)
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            for (i in (12..23).reversed()) {
                TrianglePosition(
                    position = i,
                    positionData = positions[i],
                    isSelected = selectedPosition == i,
                    isPossibleMove = possibleMoves.contains(i),
                    onClick = { onPositionClick(i) }
                )
            }
        }

        // Разделитель (бар)
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(ComposeColor.Black)
        )

        // Нижняя часть доски (позиции 0-11)
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            for (i in 0..11) {
                TrianglePosition(
                    position = i,
                    positionData = positions[i],
                    isSelected = selectedPosition == i,
                    isPossibleMove = possibleMoves.contains(i),
                    onClick = { onPositionClick(i) }
                )
            }
        }
    }
}

@Composable
fun RowScope.TrianglePosition(
    position: Int,
    positionData: PositionOnBoard,
    isSelected: Boolean,
    isPossibleMove: Boolean,
    onClick: () -> Unit
) {
    val triangleColor = if ((position / 6) % 2 == 0) {
        if (position % 2 == 0) ComposeColor.DarkGray else ComposeColor.LightGray
    } else {
        if (position % 2 == 0) ComposeColor.LightGray else ComposeColor.DarkGray
    }

    val borderColor = when {
        isSelected -> ComposeColor.Yellow
        isPossibleMove -> ComposeColor.Green
        else -> ComposeColor.Transparent
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .background(triangleColor)
            .border(2.dp, borderColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (positionData.count > 0) {
                for (i in 0 until minOf(positionData.count, 5)) {
                    Checker(color = positionData.color)
                }

                if (positionData.count > 5) {
                    Text(
                        text = "+${positionData.count - 5}",
                        color = ComposeColor.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun Checker(color: Color) {
    val checkerColor = when (color) {
        Color.BLACK -> ComposeColor.Black
        Color.WHITE -> ComposeColor.White
        Color.NEUTRAL -> ComposeColor.Transparent
    }

    Box(
        modifier = Modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(checkerColor)
            .border(1.dp, ComposeColor.DarkGray, CircleShape)
    )
}

@Composable
fun GameOverDialog(
    winner: Color?,
    onPlayAgain: () -> Unit,
    onNavigateToMainMenu: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Игра окончена") },
        text = {
            Text(
                "Победитель: ${
                    when(winner) {
                        Color.BLACK -> "Чёрные"
                        Color.WHITE -> "Белые"
                        else -> "Неизвестно"
                    }
                }"
            )
        },
        confirmButton = {
            Button(
                onClick = onPlayAgain
            ) {
                Text("Новая игра")
            }
        },
        dismissButton = {
            Button(
                onClick = onNavigateToMainMenu
            ) {
                Text("Главное меню")
            }
        }
    )
}