package com.example.movieapp.model


data class MovieDetail(
    val id: Int,
    val title: String,
    val imageUrl: String, // Add this property for the image URL
    val year: String, // Add other properties you need
    val overview: String
)
