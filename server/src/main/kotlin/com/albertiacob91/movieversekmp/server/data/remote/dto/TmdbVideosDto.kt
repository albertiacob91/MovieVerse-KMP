package com.albertiacob91.movieversekmp.server.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TmdbVideosDto(
    val results: List<TmdbVideoDto> = emptyList()
)

@Serializable
data class TmdbVideoDto(
    val key: String,
    val name: String,
    val site: String,
    val type: String,
    val official: Boolean? = null
)