package com.example.movieapp.movie

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Response<ApiResponse<Movie>>
}

data class ApiResponse<T>(
    val results: List<T>
)

data class Movie(
    val id: Int,
    val title: String,
    // Add other properties you need
)
