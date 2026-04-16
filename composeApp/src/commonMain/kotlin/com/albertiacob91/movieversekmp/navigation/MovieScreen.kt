package com.albertiacob91.movieversekmp.navigation

sealed class MovieScreen {
    data object List : MovieScreen()
    data class Detail(val movieId: Int) : MovieScreen()
    data object Favorites : MovieScreen()
    data object Profile : MovieScreen()
}