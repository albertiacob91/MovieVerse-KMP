package com.albertiacob91.movieversekmp.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class MovieDto(
    val id: Int,
    val title: String,
    val posterUrl: String? = null,
    val overview: String = "",
    val releaseDate: String? = null,
    val voteAverage: Double? = null,
    val runtime: Int? = null,
    val genres: List<String> = emptyList(),
    val cast: List<CastMemberDto> = emptyList(),
    val trailerUrl: String? = null
)