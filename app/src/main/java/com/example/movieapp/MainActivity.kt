package com.example.movieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.movieapp.database.AccountDao
import com.example.movieapp.database.FavouriteDao
import com.example.movieapp.database.MyApplication
import com.example.movieapp.model.Movie
import com.example.movieapp.movie.ApiUtils
import com.example.movieapp.movie.TmdbApiManager.tmdbApiService
import com.example.movieapp.navigations.NavigationScreen
import com.example.movieapp.ui.theme.MovieAppTheme
import okhttp3.Headers


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val myApp = application as MyApplication
        val appDatabase = myApp.database
        val accountDao = appDatabase.accountDao()
        val favouriteDao = appDatabase.favouriteDao()
        setContent {
            val navController = rememberNavController() // Initialize NavController inside the setContent block
            MovieAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainContent(accountDao = accountDao, favouriteDao = favouriteDao, navController = navController)
                }
            }
        }
    }
}
@Composable
fun MainContent(accountDao: AccountDao, favouriteDao: FavouriteDao, navController: NavController) {
    Column {
        NavigationScreen(accountDao, favouriteDao)
        HomeScreen(navController)
    }
}
@Composable
fun HomeScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    var movieList by remember { mutableStateOf<List<Movie>>(emptyList()) }

    var currentPage by remember { mutableStateOf(1) }
    var isFetching by remember { mutableStateOf(false) }
    var totalPages by remember { mutableStateOf(1) }

    // Create a state for LazyGridState
    val lazyGridState = rememberLazyGridState( )

    // Fetch movie data using the API
    LaunchedEffect(currentPage) {
        if (!isFetching && currentPage <= totalPages) {
            isFetching = true
            val response = tmdbApiService.getPopularMovies(ApiUtils.tmdbApiKey, page = currentPage)
            if (response.isSuccessful) {
                val movieResponseList = response.body()?.results ?: emptyList()

                // Extract total_pages value from response headers
                val headers: Headers = response.headers()
                totalPages = headers["X-Total-Pages"]?.toInt() ?: 1

                movieList += movieResponseList.map { movieResponse ->
                    Movie(
                        id = movieResponse.id,
                        title = movieResponse.title,
                        imageUrl = "https://image.tmdb.org/t/p/w500${movieResponse.poster_path}",
                        year = movieResponse.release_date.substring(0, 4)
                    )
                }
            }
            isFetching = false
        }
    }

    Column(
        modifier = Modifier.padding(top = 50.dp) // Add padding at the top
    ) {
        Spacer(modifier = Modifier.height(16.dp)) // Add spacer with desired padding value
        // Display fetched movie data here
        LazyVerticalGrid(
            state = lazyGridState,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            columns = GridCells.Fixed(2), // 2 columns
            content = {
                items(movieList) { movie ->
                    MovieCard(movie = movie, onMovieClick = {
                        navController.navigate("detail/${movie.id}")
                    })
                }

                // Load more items when reaching the end of the list
                if (!isFetching && currentPage < totalPages) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        )
                    }
                }
            }
        )
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