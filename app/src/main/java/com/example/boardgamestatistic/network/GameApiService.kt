package com.example.boardgamestatistic.network

import com.example.boardgamestatistic.models.Game
import com.example.boardgamestatistic.models.BoardGame
import retrofit2.Call
import retrofit2.http.*

interface GameApiService {

    @GET("games/")
    fun getGames(): Call<List<Game>>

    @GET("games/{id}")
    fun getGameById(@Path("id") id: Int): Call<Game>

    @POST("games/")
    fun createGame(@Body game: Game): Call<Game>

    @GET("boardgames/")
    fun getBoardGames(
        @Query("search") search: String? = null,
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 10
    ): Call<List<BoardGame>>

    @GET("boardgames/{id}")
    fun getBoardGameById(@Path("id") id: Int): Call<BoardGame>
}