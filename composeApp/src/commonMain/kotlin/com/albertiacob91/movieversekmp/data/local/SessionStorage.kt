package com.albertiacob91.movieversekmp.data.local

import com.russhwolf.settings.Settings

class SessionStorage(
    private val settings: Settings = Settings()
) {
    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
    }

    fun saveToken(token: String) {
        settings.putString(KEY_AUTH_TOKEN, token)
    }

    fun getToken(): String? {
        return settings.getStringOrNull(KEY_AUTH_TOKEN)
    }

    fun clearSession() {
        settings.remove(KEY_AUTH_TOKEN)
    }

    fun isLoggedIn(): Boolean {
        return !getToken().isNullOrBlank()
    }
}