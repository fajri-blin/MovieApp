package com.example.movieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.movieapp.database.AccountDao
import com.example.movieapp.database.MyApplication
import com.example.movieapp.model.Movie
import com.example.movieapp.movie.ApiUtils
import com.example.movieapp.movie.TmdbApiManager.tmdbApiService
import com.example.movieapp.navigations.NavigationScreen
import com.example.movieapp.ui.theme.MovieAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val myApp = application as MyApplication
        val appDatabase = myApp.database
        val accountDao = appDatabase.accountDao()
        setContent {
            val navController = rememberNavController() // Initialize NavController inside the setContent block
            MovieAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainContent(accountDao = accountDao, navController = navController)
                }
            }
        }
    }
}
@Composable
fun MainContent(accountDao: AccountDao, navController: NavController) {
    Column {
        NavigationScreen(accountDao)
        HomeScreen(navController)
    }
}
@Composable
fun HomeScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    var movieList by remember { mutableStateOf<List<Movie>>(emptyList()) }

    // Fetch movie data using the API
    LaunchedEffect(Unit) {
        val response = tmdbApiService.getPopularMovies(ApiUtils.tmdbApiKey, page = 1)
        if (response.isSuccessful) {
            val movieResponseList = response.body()?.results ?: emptyList()
            movieList = movieResponseList.map { movieResponse ->
                Movie(
                    id = movieResponse.id,
                    title = movieResponse.title,
                    imageUrl = "https://image.tmdb.org/t/p/w500${movieResponse.poster_path}",
                    year = movieResponse.release_date.substring(0, 4) // Extract the year
                    // Add other properties as needed
                )
            }
        }
    }

    Column(
        modifier = Modifier.padding(top = 50.dp) // Add padding at the top
    ) {
        Spacer(modifier = Modifier.height(16.dp)) // Add spacer with desired padding value
        // Display fetched movie data here
        LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)){
            items(movieList) { movie ->
                MovieCard(movie = movie, onMovieClick = {
                    navController.navigate("detail/${movie.id}")
                })
            }
        }
    }
}

@Composable
fun MovieCard(movie: Movie, onMovieClick: () -> Unit) {
    Card(
        modifier = Modifier
            .clickable { onMovieClick.invoke() }
            .padding(8.dp)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Image(
                painter = rememberImagePainter(data = movie.imageUrl, builder = {
                    crossfade(true)
                }),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(shape = RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = movie.title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Year Published: ${movie.year}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}