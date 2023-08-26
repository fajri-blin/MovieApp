package com.example.movieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.movieapp.database.AccountDao
import com.example.movieapp.database.MyApplication
import com.example.movieapp.model.Movie
import com.example.movieapp.movie.ApiUtils
import com.example.movieapp.movie.TmdbApiManager
import com.example.movieapp.movie.TmdbApiManager.tmdbApiService
import com.example.movieapp.navigations.NavigationScreen
import com.example.movieapp.ui.theme.MovieAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val myApp = application as MyApplication
        val appDatabase = myApp.database
        val accountDao = appDatabase.accountDao()
        setContent {
            MovieAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainContent(accountDao = accountDao)
                }
            }
        }
    }
}
@Composable
fun MainContent(accountDao: AccountDao) {
    Column {
        NavigationScreen(accountDao)
        HomeScreen()
    }
}
@Composable
fun HomeScreen() {
    val scope = rememberCoroutineScope()
    var movieList by remember { mutableStateOf<List<Movie>>(emptyList()) }

    // Fetch movie data using the API
    LaunchedEffect(Unit) {
        val response = tmdbApiService.getPopularMovies(ApiUtils.tmdbApiKey, page = 1)
        if (response.isSuccessful) {
            movieList = (response.body()?.results ?: emptyList()) as List<Movie>
        }
    }

    Column(
        modifier = Modifier.padding(top = 64.dp) // Adjust the value as needed
    ) {
        // Display fetched movie data here
        movieList.forEach { movie ->
            Text(text = movie.title)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MovieAppTheme {
        HomeScreen()
    }
}