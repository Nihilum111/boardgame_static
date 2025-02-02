package com.example.boardgamestatistic.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.boardgamestatistic.viewmodel.GameViewModel

@Composable
fun DetailScreen(
    gameId: Int,
    navController: NavController,
    gameViewModel: GameViewModel
) {
    val gameLiveData = gameViewModel.getGameById(gameId)
    val game by gameLiveData.observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали игры") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            game?.let { gameDetails ->
                Text(text = "Название: ${gameDetails.name}", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Дата: ${gameDetails.date}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Игроки: ${gameDetails.players}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Очки: ${gameDetails.scores}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Места: ${gameDetails.places}", style = MaterialTheme.typography.bodyLarge)
                // Добавьте дополнительные поля по необходимости
            } ?: run {
                Text(text = "Игра не найдена")
            }
        }
    }
}
