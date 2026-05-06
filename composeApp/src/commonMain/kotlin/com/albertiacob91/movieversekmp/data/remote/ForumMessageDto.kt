package com.albertiacob91.movieversekmp.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ForumMessageDto(
    val id: String,
    val chatId: String,
    val content: String,
    val username: String,
    val userId: String,
    val createdAt: String
)
