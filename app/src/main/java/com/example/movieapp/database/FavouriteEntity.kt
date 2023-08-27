package com.example.movieapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite")
data class FavouriteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val account_id: Int,
    val movie_name: String,
    val movie_id: Int,
)