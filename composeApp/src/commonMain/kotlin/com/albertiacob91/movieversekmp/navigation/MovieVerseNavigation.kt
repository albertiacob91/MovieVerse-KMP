package com.albertiacob91.movieversekmp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.albertiacob91.movieversekmp.data.remote.AuthApi
import com.albertiacob91.movieversekmp.presentation.screens.auth.LoginScreen
import com.albertiacob91.movieversekmp.presentation.screens.auth.RegisterScreen

@Composable
fun MovieVerseNavigation() {
    var currentScreen by remember { mutableStateOf<AuthScreen>(AuthScreen.Login) }
    val authApi = remember { AuthApi() }

    when (currentScreen) {
        AuthScreen.Login -> {
            LoginScreen(
                onNavigateToRegister = {
                    currentScreen = AuthScreen.Register
                },
                onLoginClick = { email, password ->
                    runCatching {
                        val response = authApi.login(email, password)
                        response.message + (response.token?.let { " | Token received" } ?: "")
                    }.getOrElse {
                        "Login error: ${it.message}"
                    }
                }
            )
        }

        AuthScreen.Register -> {
            RegisterScreen(
                onNavigateToLogin = {
                    currentScreen = AuthScreen.Login
                },
                onRegisterClick = { username, email, password ->
                    runCatching {
                        val response = authApi.register(username, email, password)
                        response.message
                    }.getOrElse {
                        "Register error: ${it.message}"
                    }
                }
            )
        }
    }
}