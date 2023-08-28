@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package com.example.movieapp.navigations

import DetailScreen
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
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
    var accountId: Int? = null

    val sharedPreferences = LocalContext.current.getSharedPreferences("session", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

//    Log.d("sharedPreferences","$sharedPreferences")

    loggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
//    Log.d("isLoggedIn","$loggedIn")

    accountId = sharedPreferences.getInt("accountId", -1) // -1 or any default value
//    Log.d("accountId","$accountId")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Movie App") },
                actions = {
                    loggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
                    Log.d("isLoggedIn","$loggedIn")
                    if(loggedIn){
                        IconButton(onClick = {
                            navController.navigate("favourites")
                        }) {
                            Icon(imageVector = Icons.Default.Favorite, contentDescription = "Favourite")
                        }
                        IconButton(onClick = {
                            loggedIn = false
                            accountId = null
                            editor.putBoolean("isLoggedIn", false)
                            editor.putInt("accountId", -1) // Store the accountId
                            editor.apply()
                            // You can also navigate to the login screen here if needed
                            Log.d("Logged Status","loggedIn = $loggedIn, accountId = $accountId")
                        }) {
                            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Logout")
                        }
                    }
                    NavigationActions(navController = navController)
                }
            )
        }
    ) {
        NavHost(navController = navController, startDestination = "home") {
            composable("home") { HomeScreen(navController) }

            composable("login") {
                LoginScreen(accountDao, navController)
            }

            composable("register") { RegisterScreen(navController) }

            composable("detail/{movieId}") { backStackEntry ->
                val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull()
                accountId = sharedPreferences.getInt("accountId", -1)
                movieId?.let { id ->
                    accountId = sharedPreferences.getInt("accountId", -1)
                    Log.d("accountId","$accountId")
                    DetailScreen(movieId = id, accountId , favouriteDao, navController = navController)
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
