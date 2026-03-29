package com.albertiacob91.movieversekmp.domain.model

data class Comment(
    val id: String,
    val movieId: Int,
    val userId: String,
    val content: String
)