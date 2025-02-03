package com.example.boardgamestatistic.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.boardgamestatistic.models.BoardGame
import com.example.boardgamestatistic.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardGamesScreen(
    navController: NavController,
    onNavigateBack: () -> Unit = {} // Callback для перехода назад в Pager
) {
    var filterText by remember { mutableStateOf("") }
    var boardGamesList by remember { mutableStateOf(listOf<BoardGame>()) }
    val context = LocalContext.current

    // Обработчик системной кнопки "назад"
    BackHandler {
        onNavigateBack()
    }

    // Функция для получения данных с фильтром
    fun fetchBoardGames() {
        RetrofitInstance.api.getBoardGames(filterText)
            .enqueue(object : Callback<List<BoardGame>> {
                override fun onResponse(
                    call: Call<List<BoardGame>>,
                    response: Response<List<BoardGame>>
                ) {
                    if (response.isSuccessful) {
                        boardGamesList = response.body() ?: listOf()
                    } else {
                        Toast.makeText(context, "Ошибка: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<BoardGame>>, t: Throwable) {
                    Toast.makeText(context, "Ошибка запроса", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // При первом запуске и при изменении текста фильтра будем получать данные
    LaunchedEffect(filterText) {
        fetchBoardGames()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настольные игры") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = filterText,
                onValueChange = { filterText = it },
                label = { Text("Фильтр по названию") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(boardGamesList) { boardGame ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .clickable {
                                // При клике переходим на новый экран с деталями
                                navController.navigate("boardGameDetail/${boardGame.id}")
                            }
                    ) {
                        Text(
                            text = boardGame.title,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
} 