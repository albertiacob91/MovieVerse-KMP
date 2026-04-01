package com.albertiacob91.movieversekmp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.albertiacob91.movieversekmp.presentation.screens.auth.LoginScreen
import com.albertiacob91.movieversekmp.presentation.screens.auth.RegisterScreen

@Composable
fun MovieVerseNavigation() {
    var currentScreen by remember { mutableStateOf<AuthScreen>(AuthScreen.Login) }

    when (currentScreen) {
        AuthScreen.Login -> {
            LoginScreen(
                onNavigateToRegister = {
                    currentScreen = AuthScreen.Register
                },
                onLoginClick = { email, password ->
                    println("Login clicked: $email / $password")
                }
            )
        }

        AuthScreen.Register -> {
            RegisterScreen(
                onNavigateToLogin = {
                    currentScreen = AuthScreen.Login
                },
                onRegisterClick = { username, email, password ->
                    println("Register clicked: $username / $email / $password")
                }
            )
        }
    }
}