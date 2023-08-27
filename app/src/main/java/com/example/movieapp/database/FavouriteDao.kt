package com.example.movieapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
@Dao
interface FavouriteDao {
    @Insert
    suspend fun insertFavourite(favourite: FavouriteEntity)

    @Query("DELETE FROM favourite WHERE id = :favoriteId")
    suspend fun deleteFavoriteById(favoriteId: Int)

    @Query("SELECT * FROM favourite WHERE account_id = :account_id")
    suspend fun getFavourite(account_id: Int?): List<FavouriteEntity>?

    @Query("SELECT * FROM favourite WHERE movie_id = :movie_id AND account_id = :account_id")
    suspend fun  checkFavouriteMovie(movie_id: Int?, account_id: Int?): FavouriteEntity?
}