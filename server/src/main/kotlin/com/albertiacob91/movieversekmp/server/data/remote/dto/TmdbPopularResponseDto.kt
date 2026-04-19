package com.albertiacob91.movieversekmp.server.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TmdbPopularResponseDto(
    val results: List<TmdbMovieDto> = emptyList()
)