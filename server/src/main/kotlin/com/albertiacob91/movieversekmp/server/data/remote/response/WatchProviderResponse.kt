package com.albertiacob91.movieversekmp.server.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class WatchProviderResponse(
    val name: String,
    val logoUrl: String?,
    val type: String
)
