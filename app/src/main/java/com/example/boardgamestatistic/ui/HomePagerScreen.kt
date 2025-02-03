package com.example.boardgamestatistic.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.boardgamestatistic.viewmodel.GameViewModel
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@Composable
fun HomePagerScreen(
    navController: NavController,
    gameViewModel: GameViewModel
) {
    val pagerState = rememberPagerState(initialPage = 0)
    val scope = rememberCoroutineScope()

    HorizontalPager(
        count = 2,
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        when (page) {
            0 -> {
                PartyListScreen(navController = navController, gameViewModel = gameViewModel)
            }
            1 -> {
                BoardGamesScreen(
                    navController = navController,
                    onNavigateBack = {
                        scope.launch { pagerState.animateScrollToPage(0) }
                    }
                )
            }
        }
    }
} 