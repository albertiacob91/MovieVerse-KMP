package com.albertiacob91.movieversekmp.server.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class CastMemberResponse(
    val id: Int,
    val name: String,
    val character: String? = null,
    val profileUrl: String? = null
)