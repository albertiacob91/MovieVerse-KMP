package com.albertiacob91.movieversekmp.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class ForumChatResponse(
    val id: String,
    val title: String,
    val createdBy: String,
    val userId: String,
    val createdAt: String
)