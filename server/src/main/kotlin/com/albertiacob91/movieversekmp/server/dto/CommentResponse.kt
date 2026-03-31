package com.albertiacob91.movieversekmp.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class CommentResponse(
    val id: String,
    val movieId: Int,
    val userId: String,
    val username: String,
    val content: String
)