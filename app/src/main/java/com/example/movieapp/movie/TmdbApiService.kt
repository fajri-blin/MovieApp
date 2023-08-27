package com.example.movieapp.movie

import com.example.movieapp.model.Movie
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Response<ApiResponse<MovieResponse>>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Response<MovieDetailResponse>
}

data class ApiResponse<T>(
    val results: List<T>
)

data class MovieResponse(
    val id: Int,
    val title: String,
    val poster_path: String, // The image URL
    val release_date: String // You might need to parse this for the year
    // Add other properties you need
)

data class MovieDetailResponse(
    val id: Int,
    val title: String,
    val backdrop_path: String?,
    val poster_path: String?,
    val release_date: String?,
    val overview: String?,
    // Add other properties you need for movie details
)

