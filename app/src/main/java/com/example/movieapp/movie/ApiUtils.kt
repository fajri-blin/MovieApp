// ApiUtils.kt

package com.example.movieapp.movie

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val apiKey = "YOUR_API_KEY"

val retrofit = Retrofit.Builder()
    .baseUrl("https://api.themoviedb.org/3/") // Base URL of the TMDB API
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val tmdbApiService = retrofit.create(TmdbApiService::class.java)
