package com.example.boardgamestatistic.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.boardgamestatistic.models.BoardGame
import com.example.boardgamestatistic.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardGameDetailScreen(
    navController: NavController,
    gameId: Int
) {
    var boardGame by remember { mutableStateOf<BoardGame?>(null) }
    val context = LocalContext.current

    LaunchedEffect(gameId) {
        RetrofitInstance.api.getBoardGameById(gameId)
            .enqueue(object : Callback<BoardGame> {
                override fun onResponse(call: Call<BoardGame>, response: Response<BoardGame>) {
                    if (response.isSuccessful) {
                        boardGame = response.body()
                    } else {
                        Toast.makeText(context, "Ошибка: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<BoardGame>, t: Throwable) {
                    Toast.makeText(context, "Ошибка запроса", Toast.LENGTH_SHORT).show()
                }
            })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детальное описание") },
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
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp)
        ) {
            boardGame?.let { game ->
                Text(text = game.title, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = game.short_description ?: "Нет описания", style = MaterialTheme.typography.bodyLarge)
            } ?: run {
                Text("Загрузка данных...")
            }
        }
    }
} 