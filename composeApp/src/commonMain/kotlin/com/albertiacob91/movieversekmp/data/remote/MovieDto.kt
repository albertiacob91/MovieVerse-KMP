package com.albertiacob91.movieversekmp.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class MovieDto(
    val id: Int,
    val title: String,
    val posterUrl: String? = null,
    val overview: String = "",
    val releaseDate: String? = null
)