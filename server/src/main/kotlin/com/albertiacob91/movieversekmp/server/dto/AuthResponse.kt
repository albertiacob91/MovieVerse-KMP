package com.albertiacob91.movieversekmp.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val message: String,
    val userId: String? = null,
    val username: String? = null
)