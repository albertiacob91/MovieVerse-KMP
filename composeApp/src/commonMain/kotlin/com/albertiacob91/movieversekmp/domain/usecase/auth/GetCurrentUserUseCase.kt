package com.albertiacob91.movieversekmp.domain.usecase.auth

import com.albertiacob91.movieversekmp.data.local.SessionStorage
import com.albertiacob91.movieversekmp.data.remote.MeResponseDto
import com.albertiacob91.movieversekmp.domain.repository.AuthRepository

class GetCurrentUserUseCase(
    private val repository: AuthRepository,
    private val sessionStorage: SessionStorage
) {
    suspend operator fun invoke(): MeResponseDto? {
        val token = sessionStorage.getToken() ?: return null
        return repository.getMe(token)
    }
}
