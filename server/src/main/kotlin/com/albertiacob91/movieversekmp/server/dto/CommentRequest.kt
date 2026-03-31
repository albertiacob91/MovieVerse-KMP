package com.albertiacob91.movieversekmp.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class CommentRequest(
    val movieId: Int,
    val content: String
)