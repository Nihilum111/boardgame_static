package com.example.boardgamestatistic.models

data class BoardGame(
    val id: Int = 0,
    val title: String,
    val short_description: String? = null
) 