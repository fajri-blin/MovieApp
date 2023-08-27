@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package com.example.movieapp.navigations

import DetailScreen
import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.movieapp.FavouriteScreen
import com.example.movieapp.HomeScreen
import com.example.movieapp.LoginScreen
import com.example.movieapp.RegisterScreen
import com.example.movieapp.database.AccountDao
import com.example.movieapp.database.FavouriteDao

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NavigationScreen(accountDao: AccountDao, favouriteDao: FavouriteDao) {
    val navController = rememberNavController()
    var loggedIn by remember { mutableStateOf(false) }
    var accountId by remember {mutableStateOf<Int?>(null)}

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Movie App") },
                actions = {
                    if(loggedIn){
                        IconButton(onClick = {
                            navController.navigate("favourites")
                        }) {
                            Icon(imageVector = Icons.Default.Favorite, contentDescription = "Favourite")
                        }
                    }
                    NavigationActions(navController = navController)
                }
            )
        }
    ) {
        NavHost(navController = navController, startDestination = "home") {
            composable("home") { HomeScreen(navController) }
            composable("login") { backStackEntry ->
                // Retrieve the account ID from the arguments if available
                val accountIdFromArgs = backStackEntry.arguments?.getInt("accountId")
                if (accountIdFromArgs != null) {
                    accountId = accountIdFromArgs
                }

                LoginScreen(accountDao, navController) { loggedInState, accountIdState ->
                    loggedIn = loggedInState
                    accountId = accountIdState as Int?
                }
            }
            composable("register") { RegisterScreen(navController) }
            composable("detail/{movieId}") { backStackEntry ->
                val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull()
                movieId?.let { id ->
                    DetailScreen(movieId = id, navController = navController)
                } /* Handle invalid movieId */
            }
            composable("favourites") {
                FavouriteScreen(favouriteDao, accountId)
            }

        }
    }
}

@Composable
fun NavigationActions(navController: NavHostController) {
    IconButton(
        onClick = { navController.navigate("login") }
    ) {
        Icon(imageVector = Icons.Default.Lock, contentDescription = "Login")
    }
    IconButton(
        onClick = { navController.navigate("register") }
    ) {
        Icon(imageVector = Icons.Default.Person, contentDescription = "Register")
    }
}
