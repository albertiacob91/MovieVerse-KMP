package com.albertiacob91.movieversekmp.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class CreateForumChatRequestDto(
    val title: String
)