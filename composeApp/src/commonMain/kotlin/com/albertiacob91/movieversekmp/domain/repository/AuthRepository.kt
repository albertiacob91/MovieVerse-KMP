package com.albertiacob91.movieversekmp.domain.repository

import com.albertiacob91.movieversekmp.data.remote.AuthResponseDto
import com.albertiacob91.movieversekmp.data.remote.MeResponseDto

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResponseDto
    suspend fun register(username: String, email: String, password: String): AuthResponseDto
    suspend fun getMe(token: String): MeResponseDto?
    suspend fun uploadAvatar(token: String, avatarBase64: String): Boolean
}
