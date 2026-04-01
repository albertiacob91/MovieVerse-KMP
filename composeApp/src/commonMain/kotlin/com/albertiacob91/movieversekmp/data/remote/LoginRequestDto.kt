package com.albertiacob91.movieversekmp.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String
)