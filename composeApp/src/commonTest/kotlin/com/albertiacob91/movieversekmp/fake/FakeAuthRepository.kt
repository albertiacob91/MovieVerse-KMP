package com.albertiacob91.movieversekmp.fake

import com.albertiacob91.movieversekmp.data.remote.AuthResponseDto
import com.albertiacob91.movieversekmp.data.remote.MeResponseDto
import com.albertiacob91.movieversekmp.domain.repository.AuthRepository

class FakeAuthRepository : AuthRepository {
    var loginResult: AuthResponseDto = AuthResponseDto(
        message = "Login exitoso",
        userId = "user-1",
        username = "testuser",
        token = "fake-token-123"
    )
    var registerResult: AuthResponseDto = AuthResponseDto(
        message = "Registro exitoso",
        userId = "user-1",
        username = "testuser",
        token = "fake-token-123"
    )
    var getMeResult: MeResponseDto? = MeResponseDto(
        id = "user-1",
        username = "testuser",
        email = "test@example.com",
        avatarUrl = null
    )
    var uploadAvatarResult: Boolean = true

    var shouldThrow: Boolean = false
    var thrownException: Throwable = RuntimeException("Error de red")

    var lastLoginEmail: String? = null
    var lastLoginPassword: String? = null
    var lastRegisterUsername: String? = null
    var lastUploadedAvatarBase64: String? = null

    override suspend fun login(email: String, password: String): AuthResponseDto {
        lastLoginEmail = email
        lastLoginPassword = password
        if (shouldThrow) throw thrownException
        return loginResult
    }

    override suspend fun register(username: String, email: String, password: String): AuthResponseDto {
        lastRegisterUsername = username
        if (shouldThrow) throw thrownException
        return registerResult
    }

    override suspend fun getMe(token: String): MeResponseDto? {
        if (shouldThrow) throw thrownException
        return getMeResult
    }

    override suspend fun uploadAvatar(token: String, avatarBase64: String): Boolean {
        lastUploadedAvatarBase64 = avatarBase64
        if (shouldThrow) throw thrownException
        return uploadAvatarResult
    }
}
