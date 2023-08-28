package com.example.movieapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.movieapp.database.FavouriteDao
import com.example.movieapp.database.FavouriteEntity
import kotlinx.coroutines.launch

@Composable
fun FavouriteScreen(favouriteDao: FavouriteDao, accountId: Int?) {
    val couroutineScope = rememberCoroutineScope()

    var favouriteMovie by remember { mutableStateOf<List<FavouriteEntity>>(emptyList()) }

    LaunchedEffect(accountId){
        couroutineScope.launch {
            val favourites = favouriteDao.getFavourite(account_id = accountId)
            favouriteMovie = favourites ?: emptyList()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 90.dp, start = 16.dp, end = 16.dp)
    ) {
        items(favouriteMovie) { favouriteMovie ->
            FavoriteMovieCard(favouriteMovie)
        }
    }
}

@Composable
fun FavoriteMovieCard(favouriteMovie: FavouriteEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = favouriteMovie.movie_name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Movie ID: ${favouriteMovie.movie_id}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
