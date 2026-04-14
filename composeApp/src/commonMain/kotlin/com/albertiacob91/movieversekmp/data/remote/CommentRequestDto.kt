package com.albertiacob91.movieversekmp.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class CommentRequestDto(
    val movieId: Int,
    val content: String
)