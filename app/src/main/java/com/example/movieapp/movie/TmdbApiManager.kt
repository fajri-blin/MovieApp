package com.example.movieapp.movie

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TmdbApiManager {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val tmdbApiService: TmdbApiService = retrofit.create(TmdbApiService::class.java)
}
