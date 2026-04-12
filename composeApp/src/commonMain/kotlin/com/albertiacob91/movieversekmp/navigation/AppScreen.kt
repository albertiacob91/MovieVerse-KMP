package com.albertiacob91.movieversekmp.navigation

sealed class AppScreen {
    data object Auth : AppScreen()
    data object Home : AppScreen()
}