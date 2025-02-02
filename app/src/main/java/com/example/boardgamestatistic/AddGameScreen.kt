package com.example.boardgamestatistic.ui

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.boardgamestatistic.models.Game
import com.example.boardgamestatistic.viewmodel.GameViewModel
import java.util.*
import com.example.boardgamestatistic.models.PlayerScore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGameScreen(
    navController: NavController,
    gameViewModel: GameViewModel,
    onGameAdded: () -> Unit
) {
    var gameName by remember { mutableStateOf("") }
    var gameDate by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            gameDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
        },
        year,
        month,
        day
    )

    val playersScores = remember { mutableStateListOf(PlayerScore()) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Добавить игру") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            OutlinedTextField(
                value = gameName,
                onValueChange = { gameName = it },
                label = { Text("Название игры") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { datePickerDialog.show() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (gameDate.isEmpty()) "Выберите дату" else gameDate)
            }

            Spacer(modifier = Modifier.height(16.dp))

            playersScores.forEachIndexed { index, player ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = player.name,
                        onValueChange = { newName ->
                            playersScores[index] = player.copy(name = newName)
                        },
                        label = { Text("Игрок ${index + 1}") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                    )

                    OutlinedTextField(
                        value = player.score,
                        onValueChange = { newScore ->
                            playersScores[index] = player.copy(score = newScore)
                            updatePlaces(playersScores)
                        },
                        label = { Text("Очки") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                    )

                    OutlinedTextField(
                        value = player.place,
                        onValueChange = { newPlace ->
                            playersScores[index] = player.copy(place = newPlace)
                        },
                        label = { Text("Место") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = {
                    playersScores.add(PlayerScore())
                }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Добавить игрока")
                }

                IconButton(onClick = {
                    if (playersScores.size > 1) {
                        playersScores.removeLast()
                        updatePlaces(playersScores)
                    }
                }) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Удалить игрока")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (gameName.isNotEmpty() && gameDate.isNotEmpty() && playersScores.isNotEmpty()) {
                        val allPlacesFilled = playersScores.all { it.place.isNotEmpty() }
                        if (allPlacesFilled) {
                            val players = playersScores.joinToString(", ") { it.name }
                            val scores = playersScores.joinToString(", ") { it.score }
                            val places = playersScores.joinToString(", ") { it.place }

                            val newGame = Game(
                                name = gameName,
                                players = players,
                                scores = scores,
                                date = gameDate,
                                places = places
                            )
                            gameViewModel.insertGame(newGame)
                            navController.popBackStack()
                        } else {
                            Toast.makeText(
                                context,
                                "Пожалуйста, проставьте места для всех игроков",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Пожалуйста, заполните все поля",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить")
            }
        }
    }
}

// Функция для обновления мест на основе очков
fun updatePlaces(playersScores: MutableList<PlayerScore>) {
    val sortedPlayers = playersScores
        .filter { it.score.isNotEmpty() }
        .sortedByDescending { it.score.toIntOrNull() ?: 0 }

    sortedPlayers.forEachIndexed { index, player ->
        val playerIndex = playersScores.indexOf(player)
        if (playerIndex != -1) {
            playersScores[playerIndex] = player.copy(place = (index + 1).toString())
        }
    }
}
