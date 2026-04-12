package com.albertiacob91.movieversekmp.server.dto.movies

import kotlinx.serialization.Serializable

@Serializable
data class MovieResponse(
    val id: Int,
    val title: String,
    val posterUrl: String? = null,
    val overview: String = "",
    val releaseDate: String? = null
)