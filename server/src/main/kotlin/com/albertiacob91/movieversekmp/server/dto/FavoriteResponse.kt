package com.albertiacob91.movieversekmp.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class FavoriteResponse(
    val id: String,
    val movieId: Int,
    val userId: String
)