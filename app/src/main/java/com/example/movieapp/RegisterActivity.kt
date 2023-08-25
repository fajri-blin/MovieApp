package com.example.movieapp

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.movieapp.database.AccountEntity
import com.example.movieapp.database.MyApplication
import com.example.movieapp.ui.theme.MovieAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class RegistrationInput(
    val email: String,
    val password: String,
    val confirmPassword: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen() {
    val context = LocalContext.current
    val registrationInput = remember { mutableStateOf(RegistrationInput("", "", "")) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create an Account",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = registrationInput.value.email,
            onValueChange = { newValue ->
                registrationInput.value = registrationInput.value.copy(email = newValue)
            },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = registrationInput.value.password,
            onValueChange = { newValue ->
                registrationInput.value = registrationInput.value.copy(password = newValue)
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = registrationInput.value.confirmPassword,
            onValueChange = { newValue ->
                registrationInput.value = registrationInput.value.copy(confirmPassword = newValue)
            },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val application = context.applicationContext as Application
                val database = (application as MyApplication).database
                val accountDao = database.accountDao()

                val input = registrationInput.value

                if (input.password == input.confirmPassword) {
                    val accountEntity = AccountEntity(email = input.email, password = input.password)
                    CoroutineScope(Dispatchers.IO).launch {
                        accountDao.insertAccount(accountEntity)
                    }
                } else {
                    // Handle password mismatch error
                }

            }, // TODO: Handle registration
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(top = 16.dp)
        ) {
            Text(text = "Register")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    MovieAppTheme {
        RegisterScreen()
    }
}
