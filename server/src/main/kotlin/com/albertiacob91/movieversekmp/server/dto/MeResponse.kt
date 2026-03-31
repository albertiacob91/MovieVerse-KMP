package com.albertiacob91.movieversekmp.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class MeResponse(
    val id: String,
    val username: String,
    val email: String
)