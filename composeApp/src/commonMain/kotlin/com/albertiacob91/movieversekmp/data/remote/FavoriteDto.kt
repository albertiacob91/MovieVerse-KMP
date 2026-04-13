package com.albertiacob91.movieversekmp.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class FavoriteDto(
    val id: String,
    val movieId: Int,
    val userId: String
)