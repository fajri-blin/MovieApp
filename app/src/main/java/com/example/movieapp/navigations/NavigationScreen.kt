@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class
)

package com.example.movieapp.navigations

import DetailScreen
import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.movieapp.HomeScreen
import com.example.movieapp.LoginScreen
import com.example.movieapp.RegisterScreen
import com.example.movieapp.database.AccountDao

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NavigationScreen(accountDao: AccountDao) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Movie App") },
                actions = {
                    NavigationActions(navController = navController)
                }
            )
        }
    ) {
        NavHost(navController = navController, startDestination = "home") {
            composable("home") { HomeScreen(navController) }
            composable("login") { LoginScreen(accountDao) }
            composable("register") { RegisterScreen(navController) }
            composable("detail/{movieId}") { backStackEntry ->
                val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull()
                movieId?.let { id ->
                    DetailScreen(movieId = id, navController = navController)
                } /* Handle invalid movieId */
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
