package com.albertiacob91.movieversekmp.domain.usecase.auth

import com.albertiacob91.movieversekmp.data.remote.AuthResponseDto
import com.albertiacob91.movieversekmp.domain.repository.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(username: String, email: String, password: String): AuthResponseDto =
        repository.register(username, email, password)
}
