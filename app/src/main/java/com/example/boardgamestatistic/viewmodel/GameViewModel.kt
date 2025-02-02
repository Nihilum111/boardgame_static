package com.example.boardgamestatistic.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.boardgamestatistic.models.Game
import com.example.boardgamestatistic.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GameViewModel : ViewModel() {

    private val _games = MutableLiveData<List<Game>>()
    val games: LiveData<List<Game>> get() = _games

    fun fetchGames() {
        RetrofitInstance.api.getGames().enqueue(object : Callback<List<Game>> {
            override fun onResponse(call: Call<List<Game>>, response: Response<List<Game>>) {
                if (response.isSuccessful) {
                    _games.value = response.body()
                }
            }

            override fun onFailure(call: Call<List<Game>>, t: Throwable) {
                // Обработка ошибки
            }
        })
    }

    fun insertGame(game: Game) {
        RetrofitInstance.api.createGame(game).enqueue(object : Callback<Game> {
            override fun onResponse(call: Call<Game>, response: Response<Game>) {
                if (response.isSuccessful) {
                    fetchGames() // Обновляем список игр после добавления
                }
            }

            override fun onFailure(call: Call<Game>, t: Throwable) {
                // Обработка ошибки
            }
        })
    }

    fun getGameById(gameId: Int): LiveData<Game?> {
        val gameData = MutableLiveData<Game?>()
        RetrofitInstance.api.getGameById(gameId).enqueue(object : Callback<Game> {
            override fun onResponse(call: Call<Game>, response: Response<Game>) {
                if (response.isSuccessful) {
                    gameData.value = response.body()
                } else {
                    gameData.value = null
                }
            }

            override fun onFailure(call: Call<Game>, t: Throwable) {
                gameData.value = null
            }
        })
        return gameData
    }
}
