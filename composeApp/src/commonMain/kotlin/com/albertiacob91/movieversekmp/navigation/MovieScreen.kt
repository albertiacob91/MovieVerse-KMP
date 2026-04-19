package com.albertiacob91.movieversekmp.navigation

sealed class MovieScreen {
    data object Home : MovieScreen()
    data object Favorites : MovieScreen()
    data object Profile : MovieScreen()
    data class Detail(val movieId: Int) : MovieScreen()
}