package com.albertiacob91.movieversekmp.server.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbMovieDetailDto(
    val id: Int,
    val title: String,
    val overview: String = "",
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("release_date")
    val releaseDate: String? = null,
    @SerialName("vote_average")
    val voteAverage: Double? = null,
    val runtime: Int? = null,
    val genres: List<TmdbGenreDto> = emptyList()
)

@Serializable
data class TmdbGenreDto(
    val id: Int,
    val name: String
)