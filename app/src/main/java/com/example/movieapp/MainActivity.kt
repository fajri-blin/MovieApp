package com.example.movieapp

import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.ImagePainter
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
    var popularMovieList by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var topRatedMovieList by remember { mutableStateOf<List<Movie>>(emptyList()) }

    var currentPopularPage by remember { mutableStateOf(1) }
    var currentTopRatedPage by remember { mutableStateOf(1) }
    var isPopularFetching by remember { mutableStateOf(false) }
    var isTopRatedFetching by remember { mutableStateOf(false) }
    var totalPopularPages by remember { mutableStateOf(1) }
    var totalTopRatedPages by remember { mutableStateOf(1) }

    // Fetch popular movie data using the API
    LaunchedEffect(currentPopularPage) {
        if (!isPopularFetching && currentPopularPage <= totalPopularPages) {
            isPopularFetching = true
            val response = tmdbApiService.getPopularMovies(ApiUtils.tmdbApiKey, currentPopularPage)
            if (response.isSuccessful) {
                val responseBody = response.body()

                if (responseBody != null) {
                    val movieResponseList = responseBody.results

                    val headers: Headers = response.headers()
                    totalPopularPages = headers["X-Total-Pages"]?.toInt() ?: 1

                    popularMovieList += movieResponseList.map { movieResponse ->
                        Movie(
                            id = movieResponse.id,
                            title = movieResponse.title,
                            imageUrl = "https://image.tmdb.org/t/p/w500${movieResponse.poster_path}",
                            year = movieResponse.release_date.substring(0, 4)
                        )
                    }

                    Log.d("popularMovieList","$popularMovieList")

                    currentPopularPage++
                }
            }
            isPopularFetching = false
        }
    }

    // Fetch top-rated movie data using the API
    LaunchedEffect(currentTopRatedPage) {
        if (!isTopRatedFetching && currentTopRatedPage <= totalTopRatedPages) {
            isTopRatedFetching = true
            val response = tmdbApiService.getTopRatedMovie(ApiUtils.tmdbApiKey, currentTopRatedPage)
            if (response.isSuccessful) {
                val responseBody = response.body()

                if (responseBody != null) {
                    val movieResponseList = responseBody.results

                    movieResponseList.forEach { movieResponse ->
                        Log.d("Poster Path", "Movie ID: ${movieResponse.id}, Poster Path: ${movieResponse.poster_path}")
                    }

                    val headers: Headers = response.headers()
                    totalTopRatedPages = headers["X-Total-Pages"]?.toInt() ?: 1

                    topRatedMovieList += movieResponseList.map { movieResponse ->
                        Movie(
                            id = movieResponse.id,
                            title = movieResponse.title,
                            imageUrl = "https://image.tmdb.org/t/p/w500${movieResponse.poster_path}",
                            year = movieResponse.release_date.substring(0, 4)
                        )
                    }
                    Log.d("topRatedMovie","$topRatedMovieList")

                    currentTopRatedPage++
                }
            }
            isTopRatedFetching = false
        }
    }

    Column(
        modifier = Modifier.padding(top = 50.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Display top-rated movie section with horizontal scrolling
        Text(
            text = "Top Rated Movies",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(
            state = rememberLazyListState(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            content = {
                items(topRatedMovieList) { movie ->
                    MovieCard(movie = movie, onMovieClick = {
                        navController.navigate("detail/${movie.id}")
                    })
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Display popular movie section with vertical scrolling
        Text(
            text = "Popular Movies",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyVerticalGrid(
            state = rememberLazyGridState(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            columns = GridCells.Fixed(2),
            content = {
                items(popularMovieList) { movie ->
                    MovieCard(movie = movie, onMovieClick = {
                        navController.navigate("detail/${movie.id}")
                    })
                }

                if (!isPopularFetching && currentPopularPage < totalPopularPages) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
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
            val context = LocalContext.current

            Image(
                painter = rememberImagePainter(
                    data = movie.imageUrl
                    ),
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