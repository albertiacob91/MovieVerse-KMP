package com.albertiacob91.movieversekmp.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class WatchProviderDto(
    val name: String,
    val logoUrl: String?,
    val type: String
)
