package com.albertiacob91.movieversekmp.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseDto(
    val message: String,
    val userId: String? = null,
    val username: String? = null,
    val token: String? = null
)