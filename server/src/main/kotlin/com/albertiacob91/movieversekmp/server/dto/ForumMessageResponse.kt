package com.albertiacob91.movieversekmp.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class ForumMessageResponse(
    val id: String,
    val chatId: String,
    val content: String,
    val username: String,
    val userId: String,
    val createdAt: String
)