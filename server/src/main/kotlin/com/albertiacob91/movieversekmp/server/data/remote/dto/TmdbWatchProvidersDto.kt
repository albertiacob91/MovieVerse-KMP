package com.albertiacob91.movieversekmp.server.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbWatchProvidersDto(
    val results: Map<String, TmdbRegionProvidersDto> = emptyMap()
)

@Serializable
data class TmdbRegionProvidersDto(
    val flatrate: List<TmdbProviderDto> = emptyList(),
    val rent: List<TmdbProviderDto> = emptyList(),
    val buy: List<TmdbProviderDto> = emptyList()
)

@Serializable
data class TmdbProviderDto(
    @SerialName("provider_name")
    val providerName: String,
    @SerialName("logo_path")
    val logoPath: String? = null
)
