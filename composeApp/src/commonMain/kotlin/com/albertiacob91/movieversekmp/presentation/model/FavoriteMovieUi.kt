package com.albertiacob91.movieversekmp.presentation.model

data class FavoriteMovieUi(
    val movieId: Int,
    val title: String,
    val releaseDate: String? = null
)