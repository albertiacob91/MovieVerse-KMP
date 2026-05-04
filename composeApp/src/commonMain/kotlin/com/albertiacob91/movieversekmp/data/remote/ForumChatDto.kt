package com.albertiacob91.movieversekmp.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ForumChatDto(
    val id: String,
    val title: String,
    val createdBy: String,
    val createdAt: String
)