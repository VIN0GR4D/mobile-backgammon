package com.example.backgammon.core

class Dices {
    fun rollDices(): MutableList<Int> {
        val firstDice = (Math.random() * 6 + 1).toInt()
        val secondDice = (Math.random() * 6 + 1).toInt()
        return if (firstDice == secondDice) mutableListOf(firstDice, firstDice, secondDice, secondDice)
        else mutableListOf(firstDice, secondDice)
    }
}