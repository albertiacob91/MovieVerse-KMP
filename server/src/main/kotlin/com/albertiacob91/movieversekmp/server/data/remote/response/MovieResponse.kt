package com.albertiacob91.movieversekmp.server.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class MovieResponse(
    val id: Int,
    val title: String,
    val posterUrl: String? = null,
    val overview: String = "",
    val releaseDate: String? = null,
    val voteAverage: Double? = null,
    val runtime: Int? = null,
    val genres: List<String> = emptyList(),
    val cast: List<CastMemberResponse> = emptyList(),
    val trailerUrl: String? = null
)