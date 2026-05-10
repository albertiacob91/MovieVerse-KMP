package com.albertiacob91.movieversekmp.data.repository

import com.albertiacob91.movieversekmp.data.remote.AuthApi
import com.albertiacob91.movieversekmp.data.remote.AuthResponseDto
import com.albertiacob91.movieversekmp.data.remote.MeResponseDto
import com.albertiacob91.movieversekmp.domain.repository.AuthRepository

class AuthRepositoryImpl(private val api: AuthApi) : AuthRepository {
    override suspend fun login(email: String, password: String): AuthResponseDto = api.login(email, password)
    override suspend fun register(username: String, email: String, password: String): AuthResponseDto = api.register(username, email, password)
    override suspend fun getMe(token: String): MeResponseDto? = api.getMe(token)
    override suspend fun uploadAvatar(token: String, avatarBase64: String): Boolean = api.uploadAvatar(token, avatarBase64)
}
