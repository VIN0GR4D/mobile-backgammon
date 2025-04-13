package com.example.backgammon.ui.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.backgammon.core.Color
import com.example.backgammon.core.PositionOnBoard
import com.example.backgammon.viewmodel.GameViewModel
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.delay

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
            val isRollingDice = gameState.isRollingDice

            gameState.diceValues.forEach { diceValue ->
                DiceView(
                    value = diceValue,
                    isRolling = isRollingDice
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            // Если кости ещё не брошены и не идёт анимация, отображаем кнопку
            if (gameState.diceValues.isEmpty() && !isRollingDice) {
                Button(
                    onClick = { gameViewModel.rollDice() },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Бросить кости")
                }
            }
        }

        // Игровое поле
        BackgammonBoard(
            positions = gameState.positions,
            selectedPosition = gameState.selectedPosition,
            possibleMoves = gameState.possibleMoves,
            hints = gameState.hints,
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

        // Первый ряд кнопок управления
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { gameViewModel.updateTurns() },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "Завершить ход",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = {
                    if (gameState.hints.isEmpty()) gameViewModel.showHints() else gameViewModel.hideHints()
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = if (gameState.hints.isEmpty()) "Подсказки" else "Скрыть",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Второй ряд кнопок
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { gameViewModel.resetGame() },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "Новая игра",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = onNavigateToMainMenu,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "Главное меню",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun DiceView(value: Int, isRolling: Boolean = false) {
    val rotationState = remember { Animatable(0f) }
    val diceColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary
    )
    val colorIndex = remember { mutableStateOf(0) }

    LaunchedEffect(isRolling) {
        if (isRolling) {
            // Анимация вращения кости
            rotationState.animateTo(
                targetValue = 360f,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = LinearEasing
                )
            )
            rotationState.snapTo(0f)

            // Мигание цветом во время броска
            while(isRolling) {
                colorIndex.value = (colorIndex.value + 1) % diceColors.size
                delay(120)
            }
        }
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .graphicsLayer {
                rotationZ = rotationState.value
            }
            .background(
                color = if (isRolling) diceColors[colorIndex.value] else ComposeColor.White,
                shape = RoundedCornerShape(8.dp)
            )
            .border(1.dp, ComposeColor.Black, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = if (isRolling) ComposeColor.White else ComposeColor.Black
        )
    }
}

@Composable
fun BackgammonBoard(
    positions: List<PositionOnBoard>,
    selectedPosition: Int,
    possibleMoves: List<Int>,
    hints: List<Pair<Int, Int>> = emptyList(),
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
                    isHintSource = hints.any { it.first == i },
                    isHintDestination = hints.any { it.second == i },
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
                    isHintSource = hints.any { it.first == i },
                    isHintDestination = hints.any { it.second == i },
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
    isHintSource: Boolean = false,
    isHintDestination: Boolean = false,
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
        isHintSource -> ComposeColor.Blue.copy(alpha = 0.7f)
        isHintDestination -> ComposeColor.Cyan.copy(alpha = 0.5f)
        else -> ComposeColor.Transparent
    }

    val borderWidth = when {
        isSelected || isPossibleMove -> 2.dp
        isHintSource || isHintDestination -> 1.dp
        else -> 0.dp
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .background(triangleColor)
            .border(borderWidth, borderColor)
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