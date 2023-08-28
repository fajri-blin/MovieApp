import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.movieapp.database.FavouriteDao
import com.example.movieapp.database.FavouriteEntity
import com.example.movieapp.model.MovieDetail
import com.example.movieapp.movie.ApiUtils
import com.example.movieapp.movie.TmdbApiManager.tmdbApiService
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(movieId: Int, accountId: Int?,favouriteDao: FavouriteDao, navController: NavController) {
    val scope = rememberCoroutineScope()
    var movieDetail by remember { mutableStateOf<MovieDetail?>(null) }
    var isFavorite by remember { mutableStateOf(false) }

    // Fetch movie detail using the API
    LaunchedEffect(movieId) {
        val response = tmdbApiService.getMovieDetails(movieId, ApiUtils.tmdbApiKey)
        if (response.isSuccessful) {
            val movieDetailResponse = response.body()
            movieDetail = movieDetailResponse?.let {
                MovieDetail(
                    id = it.id,
                    title = it.title,
                    imageUrl = "https://image.tmdb.org/t/p/w500${it.poster_path}",
                    year = it.release_date?.substring(0, 4) ?: "",
                    overview = it.overview ?: ""
                )
            }
        }
        // Check favorite status here and update isFavorite accordingly
        if(accountId != null) {
            val favouriteEntity = favouriteDao.checkFavouriteMovie(movieId, accountId)
            isFavorite = favouriteEntity != null
        }
    }

    movieDetail?.let { movie ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Image(
                painter = rememberImagePainter(data = movie.imageUrl, builder = {
                    crossfade(true)
                }),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            // Circular button for adding/removing favorite
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        isFavorite = !isFavorite
                        // Update favorite status in the database
                        if (isFavorite) {
                            // Add movie to favorites
                            val favouriteEntity = FavouriteEntity(
                                account_id = accountId,
                                movie_name = movieDetail?.title ?: "",
                                movie_id = movieId
                            )
                            Log.d("favouriteEntity","$favouriteEntity")
                            favouriteDao.insertFavourite(favouriteEntity)
                        } else {
                            // Remove movie from favorites
                            Log.d("movieId","$movieId")
                            favouriteDao.deleteFavoriteById(movieId)
                        }
                    }
                    // Toggle the favorite status
                },
                modifier = Modifier
                    .padding(16.dp)
                    .size(48.dp)
            ) {
                // Display heart icon based on favorite status
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

//            // Add a back button to navigate back to the previous screen
//            IconButton(
//                onClick = { navController.popBackStack() },
//                modifier = Modifier
//                    .padding(16.dp)
//                    .size(48.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.ArrowBack,
//                    contentDescription = null,
//                    tint = MaterialTheme.colorScheme.primary
//                )
//            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Year: ${movie.year}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = movie.overview,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

}
