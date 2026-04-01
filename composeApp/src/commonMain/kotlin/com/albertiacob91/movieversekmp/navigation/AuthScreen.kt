package com.albertiacob91.movieversekmp.navigation

sealed class AuthScreen {
    data object Login : AuthScreen()
    data object Register : AuthScreen()
}