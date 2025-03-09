package com.example.backgammon.core

class Board(boardListener: BoardListenerInterface) {

    private var listener: BoardListenerInterface

    var turns: MutableList<Int> = mutableListOf()

    var currentTurn = Color.BLACK
    private var counterOfMovesFromHead = 0

    var canThrowBlack = false
    var canThrowWhite = false

    val listOfPositions = mutableListOf<PositionOnBoard>()

    init {
        listener = boardListener
        for (x in 0..23) {
                listOfPositions.add(PositionOnBoard())
        }
        listOfPositions[0].color = Color.WHITE
        listOfPositions[0].count = 15

        listOfPositions[12].color = Color.BLACK
        listOfPositions[12].count = 15
    }

    private fun move(from: Int, to: Int) {
        if (listOfPositions[to].color == listOfPositions[from].color || listOfPositions[to].color == Color.NEUTRAL) {

            listOfPositions[to].count += 1
            listOfPositions[to].color = listOfPositions[from].color

            listOfPositions[from].count -= 1
            if (listOfPositions[from].count == 0) {
                listOfPositions[from].color = Color.NEUTRAL
            }
        }
    }

    private fun getColorOfPosition(position: Int): Color {
        return listOfPositions[position % 24].color
    }

    fun updateTurns() {
        if (turns.isEmpty()) {
            currentTurn = currentTurn.opposite()
            turns = Dices().rollDices()
            listener.showDices(turns[0], turns[1])
            counterOfMovesFromHead = 0
        }
    }

    fun possibleMoves(from: Int): List<Int> {
        val result = mutableListOf<Int>()
        val fromColor = getColorOfPosition(from)
        val firstTurn: Int
        val secondTurn: Int
        if (turns.size > 1) {
            firstTurn = turns[0]
            secondTurn = turns[1]
            if (getColorOfPosition(from + firstTurn) == fromColor ||
                getColorOfPosition(from + firstTurn) == Color.NEUTRAL) {
                result.add(from + firstTurn)

                if (getColorOfPosition(from + firstTurn + secondTurn) == fromColor ||
                    getColorOfPosition(from + firstTurn + secondTurn) == Color.NEUTRAL)
                {
                    result.add(from + firstTurn + secondTurn)
                }
            }

            if (getColorOfPosition(from + secondTurn) == fromColor ||
                getColorOfPosition(from + secondTurn) == Color.NEUTRAL) {
                result.add(from + secondTurn)

                if (getColorOfPosition(from + firstTurn + secondTurn) == fromColor ||
                    getColorOfPosition(from + firstTurn + secondTurn) == Color.NEUTRAL)
                {
                    result.add(from + firstTurn + secondTurn)
                }
            }
        }
        else {
            val lastTurn = turns[0]
            if (getColorOfPosition(from + lastTurn) == fromColor ||
                getColorOfPosition(from + lastTurn) == Color.NEUTRAL) {
                result.add(from + lastTurn)
            }
        }
        if ((from == 0 && listOfPositions[from].color == Color.WHITE ||
            from == 12 && listOfPositions[from].color == Color.BLACK)
            && counterOfMovesFromHead > 0 ) {
            return emptyList()
        }

        for (i in result.indices) {
            if (result[i] >= 24) {
                result[i] = result[i] % 24
            }
        }
        //удаление ходов, которые противоречат правилам
        unableToMoveFromHome(fromColor, result, from)
        return result
    }

    fun possibleToThrow(from: Int): Boolean {
        var result = false
        val firstTurn: Int
        val secondTurn: Int
        if (turns.size > 1) {
            firstTurn = turns[0]
            secondTurn = turns[1]
            if (canThrowBlack && getColorOfPosition(from) == Color.BLACK) {
                if (from + firstTurn >= 12) {
                    result = true
                }
                if (from + secondTurn >= 12) {
                    result = true
                }
            }
            if (canThrowWhite && getColorOfPosition(from) == Color.WHITE) {
                    if (from + firstTurn >= 24) {
                        result = true
                    }
                    if (from + secondTurn >= 24) {
                        result = true
                    }
            }
        } else {
            val lastTurn = turns[0]
            if ((canThrowBlack && getColorOfPosition(from) == Color.BLACK)) {
                if (from + lastTurn >= 12) {
                result = true
                }
            }
            if ((canThrowWhite && getColorOfPosition(from) == Color.WHITE)) {
                if (from + lastTurn >= 24) {
                    result = true
                }
            }
        }
        return result
    }

    //функция, чтобы шашки не ходили бесконечно по кругу
    private fun unableToMoveFromHome(fromColor: Color, listOfAddedMoves: MutableList<Int>, from: Int) {
        val copyOfListOfAddedMoves = listOfAddedMoves.toList()
        //нужна копия т.к нельзя идти по листу и одновременно что-то в нём удалять
        if (fromColor == Color.WHITE) {
            if (from in 12..23) {
               for (i in copyOfListOfAddedMoves) {
                   if (i < 12) {
                       listOfAddedMoves.remove(i)
                   }
               }
            }
        }
        if (fromColor == Color.BLACK) {
            if (from in 0..11) {
                for (i in copyOfListOfAddedMoves) {
                    if (i >= 12) {
                        listOfAddedMoves.remove(i)
                    }
                }
            }
        }
    }

    fun checkPossibilityOfThrowing() {
        canThrowBlack = currentTurn == Color.BLACK
        for (i in 12..29) {
            if (getColorOfPosition(i) == Color.BLACK) canThrowBlack = false
        }
        canThrowWhite = currentTurn == Color.WHITE
        for (i in 0..17) {
            if (getColorOfPosition(i) == Color.WHITE) canThrowWhite = false
        }
    }

    fun makeMove(from: Int, to: Int) {
        val deferenceBetweenToAndFrom: Int
        if (to - from < 0) {
            deferenceBetweenToAndFrom = to - from + 24
        } else {
            deferenceBetweenToAndFrom = to - from
        }
        if (turns.contains(deferenceBetweenToAndFrom)) {
            turns.remove(deferenceBetweenToAndFrom)
        } else if (deferenceBetweenToAndFrom == turns[0] + turns[1]) {
            turns.remove(turns[0])
            turns.remove(turns[0])
        }
        //счётчик снятых с головы шашек
        if (from == 0 && listOfPositions[from].color == Color.WHITE ||
            from == 12 && listOfPositions[from].color == Color.BLACK) {
            counterOfMovesFromHead += 1
        }
        move(from, to)
        updateTurns()
    }

    fun throwOutFromTheBoard(from: Int) {
        listOfPositions[from].count -= 1
        val copyTurns = turns.sorted()
        if (currentTurn == Color.WHITE) {
            if (from + copyTurns[0] >= 24) {
                turns.remove(copyTurns[0])
            }
            else if  (from + copyTurns[1] >= 24) {
                turns.remove(copyTurns[1])
            }
        }
        if (currentTurn == Color.BLACK) {
            if (from + copyTurns[0] >= 12) {
                turns.remove(copyTurns[0])
            }
            else if  (from + copyTurns[1] >= 12) {
                turns.remove(copyTurns[1])
            }
        }
        if (listOfPositions[from].count == 0) {
            listOfPositions[from].color = Color.NEUTRAL
        }
    }

    fun clearAllBoard() {
        listOfPositions.clear()
        currentTurn = Color.NEUTRAL
        for (i in 0..23) {
            listOfPositions.add(PositionOnBoard())
        }
        listOfPositions[0].color = Color.WHITE
        listOfPositions[0].count = 15

        listOfPositions[12].color = Color.BLACK
        listOfPositions[12].count = 15
    }

    fun gameOverCheck(): Color? {
        var black = false
        var white = false
        var winner: Color? = null

        for (i in 0..23) {
            if (listOfPositions[i].color == Color.BLACK) black = true
            if (listOfPositions[i].color == Color.WHITE) white = true
        }
        if (!black) winner = Color.BLACK
        if (!white) winner = Color.WHITE

        return winner
    }

    operator fun get(position: Int): PositionOnBoard = listOfPositions[position]

}