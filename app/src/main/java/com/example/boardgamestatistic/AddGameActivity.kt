package com.example.boardgamestatistic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.boardgamestatistic.ui.AddGameScreen
import com.example.boardgamestatistic.viewmodel.GameViewModel

class AddGameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val gameViewModel: GameViewModel = viewModel()
            val navController = rememberNavController()

            AddGameScreen(
                navController = navController,
                gameViewModel = gameViewModel,
                onGameAdded = {
                    // Действие после добавления игры
                    finish() // Закрываем активность
                }
            )
        }
    }
}
