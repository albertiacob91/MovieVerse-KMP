package com.albertiacob91.movieversekmp.server.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbCreditsDto(
    val cast: List<TmdbCastDto> = emptyList()
)

@Serializable
data class TmdbCastDto(
    val id: Int,
    val name: String,
    val character: String? = null,
    @SerialName("profile_path")
    val profilePath: String? = null
)