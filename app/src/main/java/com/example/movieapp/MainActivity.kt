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
import androidx.compose.foundation.layout.width
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
import kotlinx.coroutines.launch
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

    // Step 1: Create a scroll state for popular movies
    val popularMovieScrollState = rememberLazyGridState()

    // Step 4: Create a scroll state for top-rated movies
    val topRatedMovieScrollState = rememberLazyListState()

    // Step 7: Define the loadMorePopularData function
    fun loadMorePopularData() {
        scope.launch {
            try {
                isPopularFetching = true
                val response = tmdbApiService.getPopularMovies(ApiUtils.tmdbApiKey, currentPopularPage)

                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null) {
                        val movieResponseList = responseBody.results
                        popularMovieList += movieResponseList.map { movieResponse ->
                            Movie(
                                id = movieResponse.id,
                                title = movieResponse.title,
                                imageUrl = "https://image.tmdb.org/t/p/w500${movieResponse.poster_path}",
                                year = movieResponse.release_date.substring(0, 4)
                            )
                        }

                        // Increment the current page
                        currentPopularPage++
                    }
                } else {
                    Log.e("LoadMore", "Response not successful: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("LoadMore", "Error loading more popular data: ${e.message}")
                // Handle any errors or exceptions here
            } finally {
                isPopularFetching = false
            }
        }
    }

    // Step 8: Define the loadMoreTopRatedData function
    fun loadMoreTopRatedData() {
        scope.launch {
            try {
                isTopRatedFetching = true
                val response = tmdbApiService.getTopRatedMovie(ApiUtils.tmdbApiKey, currentTopRatedPage)

                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null) {
                        val movieResponseList = responseBody.results
                        topRatedMovieList += movieResponseList.map { movieResponse ->
                            Movie(
                                id = movieResponse.id,
                                title = movieResponse.title,
                                imageUrl = "https://image.tmdb.org/t/p/w500${movieResponse.poster_path}",
                                year = movieResponse.release_date.substring(0, 4)
                            )
                        }

                        // Increment the current page
                        currentTopRatedPage++
                    }
                } else {
                    Log.e("LoadMore", "Response not successful: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("LoadMore", "Error loading more top-rated data: ${e.message}")
                // Handle any errors or exceptions here
            } finally {
                isTopRatedFetching = false
            }
        }
    }



    // Load initial data when the composable is first displayed
    LaunchedEffect(Unit) {
        loadMorePopularData()
        loadMoreTopRatedData()
    }

    // Step 2: Add a scroll listener to detect when the end of the list is reached for popular movies
    val popularMovieVisibleItemCount = popularMovieScrollState.layoutInfo.visibleItemsInfo.size

    // Step 5: Add a scroll listener to detect when the end of the list is reached for top-rated movies
    val topRatedMovieVisibleItemCount = topRatedMovieScrollState.layoutInfo.visibleItemsInfo.size

    // Step 3: Load more popular movies when the end is reached
    LaunchedEffect(popularMovieVisibleItemCount) {
        if (!isPopularFetching && popularMovieVisibleItemCount >= popularMovieList.size - 4 && currentPopularPage < totalPopularPages) {
            // Load more data when there are 4 or fewer items left in the list
            currentPopularPage++
            loadMorePopularData()
        }
    }

    // Step 6: Load more top-rated movies when the end is reached
    LaunchedEffect(topRatedMovieVisibleItemCount) {
        if (!isTopRatedFetching && topRatedMovieVisibleItemCount >= topRatedMovieList.size - 4 && currentTopRatedPage < totalTopRatedPages) {
            // Load more data when there are 4 or fewer items left in the list
            currentTopRatedPage++
            loadMoreTopRatedData()
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
            // Step 7: Use the scroll state for popular movies
            state = popularMovieScrollState,
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

// ... (same steps for loadMorePopularData and top-rated movies)




@Composable
fun MovieCard(movie: Movie, onMovieClick: () -> Unit) {
//    Log.d("movie.imageUrl","${movie.imageUrl}")
    Card(
        modifier = Modifier
            .clickable { onMovieClick.invoke() }
            .padding(8.dp)
            .width(200.dp)
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