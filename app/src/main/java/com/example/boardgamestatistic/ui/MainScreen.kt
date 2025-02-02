package com.example.boardgamestatistic.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.boardgamestatistic.viewmodel.GameViewModel

@Composable
fun MainScreen(navController: NavController, gameViewModel: GameViewModel) {
    val games by gameViewModel.games.observeAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        gameViewModel.fetchGames()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Список игр") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addGame") }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Добавить игру")
            }
        }
    ) { padding ->
        if (games.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Нет добавленных игр")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(games) { game ->
                    GameItem(game = game, onClick = {
                        navController.navigate("detail/${game.id}")
                    })
                }
            }
        }
    }
}
