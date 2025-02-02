package com.example.boardgamestatistic.network

import com.example.boardgamestatistic.models.Game
import retrofit2.Call
import retrofit2.http.*

interface GameApiService {

    @GET("games/")
    fun getGames(): Call<List<Game>>

    @GET("games/{id}")
    fun getGameById(@Path("id") id: Int): Call<Game>

    @POST("games/")
    fun createGame(@Body game: Game): Call<Game>
}