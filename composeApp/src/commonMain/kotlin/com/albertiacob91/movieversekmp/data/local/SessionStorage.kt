package com.albertiacob91.movieversekmp.data.local

import com.russhwolf.settings.Settings

class SessionStorage(
    private val settings: Settings = Settings()
) {
    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
    }

    fun saveToken(token: String) {
        settings.putString(KEY_AUTH_TOKEN, token)
    }

    fun getToken(): String? {
        return settings.getStringOrNull(KEY_AUTH_TOKEN)
    }

    fun saveUserId(userId: String) {
        settings.putString(KEY_USER_ID, userId)
    }

    fun getUserId(): String? {
        return settings.getStringOrNull(KEY_USER_ID)
    }

    fun clearSession() {
        settings.remove(KEY_AUTH_TOKEN)
        settings.remove(KEY_USER_ID)
    }

    fun isLoggedIn(): Boolean {
        return !getToken().isNullOrBlank()
    }
}