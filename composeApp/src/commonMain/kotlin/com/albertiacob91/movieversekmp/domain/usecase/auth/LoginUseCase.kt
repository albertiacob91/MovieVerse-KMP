package com.albertiacob91.movieversekmp.domain.usecase.auth

import com.albertiacob91.movieversekmp.data.local.SessionStorage
import com.albertiacob91.movieversekmp.data.remote.AuthResponseDto
import com.albertiacob91.movieversekmp.domain.repository.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository,
    private val sessionStorage: SessionStorage
) {
    suspend operator fun invoke(email: String, password: String): AuthResponseDto {
        val response = repository.login(email, password)
        if (!response.token.isNullOrBlank()) {
            sessionStorage.saveToken(response.token)
        }
        return response
    }
}
