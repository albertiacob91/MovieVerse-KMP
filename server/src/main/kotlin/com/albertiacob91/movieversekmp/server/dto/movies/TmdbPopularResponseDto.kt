package com.albertiacob91.movieversekmp.server.dto.movies

import kotlinx.serialization.Serializable

@Serializable
data class TmdbPopularResponseDto(
    val page: Int,
    val results: List<TmdbMovieDto>
)