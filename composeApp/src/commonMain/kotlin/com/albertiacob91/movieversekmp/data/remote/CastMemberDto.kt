package com.albertiacob91.movieversekmp.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class CastMemberDto(
    val id: Int,
    val name: String,
    val character: String? = null,
    val profileUrl: String? = null
)