package com.example.boardgamestatistic.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import com.example.boardgamestatistic.viewmodel.GameViewModel

@Composable
fun NavigationComponent() {
    val navController = rememberNavController()
    val gameViewModel: GameViewModel = viewModel()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomePagerScreen(navController = navController, gameViewModel = gameViewModel)
        }
        composable(
            route = "detail/{gameId}",
            arguments = listOf(navArgument("gameId") { type = NavType.IntType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getInt("gameId")
            if (gameId != null) {
                DetailScreen(gameId = gameId, navController = navController, gameViewModel = gameViewModel)
            }
        }
        composable("addGame") {
            AddGameScreen(
                navController = navController,
                gameViewModel = gameViewModel,
                onGameAdded = {
                    navController.popBackStack()
                }
            )
        }
    }
}
