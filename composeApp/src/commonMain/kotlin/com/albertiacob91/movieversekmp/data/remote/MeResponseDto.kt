package com.albertiacob91.movieversekmp.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class MeResponseDto(
    val id: String,
    val username: String,
    val email: String
)