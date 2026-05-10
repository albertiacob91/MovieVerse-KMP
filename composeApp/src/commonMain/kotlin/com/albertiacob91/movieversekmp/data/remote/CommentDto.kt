package com.albertiacob91.movieversekmp.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class CommentDto(
    val id: String,
    val movieId: Int,
    val userId: String,
    val username: String,
    val content: String,
    val avatarUrl: String? = null
)