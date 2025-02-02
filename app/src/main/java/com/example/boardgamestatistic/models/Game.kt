package com.example.boardgamestatistic.models

data class Game(
    val id: Int = 0,
    val name: String,
    val players: String,
    val scores: String,
    val date: String,
    val places: String
)
