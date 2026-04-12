package com.albertiacob91.movieversekmp.server.dto.movies

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbMovieDto(
    val id: Int,
    val title: String,
    @SerialName("poster_path") val posterPath: String? = null,
    val overview: String = "",
    @SerialName("release_date") val releaseDate: String? = null
)