package com.albertiacob91.movieversekmp.domain.usecase.auth

import com.albertiacob91.movieversekmp.data.local.SessionStorage
import com.albertiacob91.movieversekmp.domain.repository.AuthRepository

class UploadAvatarUseCase(
    private val repository: AuthRepository,
    private val sessionStorage: SessionStorage
) {
    suspend operator fun invoke(avatarBase64: String): Boolean {
        val token = sessionStorage.getToken() ?: return false
        return repository.uploadAvatar(token, avatarBase64)
    }
}
