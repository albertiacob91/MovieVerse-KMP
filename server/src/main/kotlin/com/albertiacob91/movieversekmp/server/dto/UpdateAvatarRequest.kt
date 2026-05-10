package com.albertiacob91.movieversekmp.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateAvatarRequest(val avatarBase64: String)
