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
    suspend fun getFavourite(account_id: Int): FavouriteEntity?

}