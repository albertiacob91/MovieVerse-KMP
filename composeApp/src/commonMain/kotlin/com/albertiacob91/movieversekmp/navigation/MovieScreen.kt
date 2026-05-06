package com.albertiacob91.movieversekmp.navigation

sealed class MovieScreen {
    data object Home : MovieScreen()
    data object Favorites : MovieScreen()
    data object Forum : MovieScreen()
    data object Profile : MovieScreen()
    data class Detail(val movieId: Int) : MovieScreen()
    data class ForumChatDetail(val chatId: String, val title: String) : MovieScreen()
}