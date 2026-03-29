package com.albertiacob91.movieversekmp.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val posterUrl: String? = null
)