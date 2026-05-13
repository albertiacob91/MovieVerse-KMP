package com.albertiacob91.movieversekmp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albertiacob91.movieversekmp.data.local.SessionStorage
import com.albertiacob91.movieversekmp.domain.usecase.auth.GetCurrentUserUseCase
import com.albertiacob91.movieversekmp.domain.usecase.auth.LoginUseCase
import com.albertiacob91.movieversekmp.domain.usecase.auth.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isCheckingSession: Boolean = true,
    val isLoggedIn: Boolean = false
)

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun checkSession() {
        viewModelScope.launch {
            val token = sessionStorage.getToken()
            if (token.isNullOrBlank()) {
                _state.update { it.copy(isCheckingSession = false, isLoggedIn = false) }
                return@launch
            }
            _state.update { it.copy(isCheckingSession = false, isLoggedIn = true) }
            val me = runCatching { getCurrentUserUseCase() }.getOrNull()
            if (me != null) {
                sessionStorage.saveUserId(me.id)
            } else {
                sessionStorage.clearSession()
                _state.update { it.copy(isLoggedIn = false) }
            }
        }
    }

    suspend fun login(email: String, password: String): String {
        return runCatching {
            val response = loginUseCase(email, password)
            if (!response.token.isNullOrBlank()) {
                _state.update { it.copy(isLoggedIn = true) }
            }
            response.message ?: ""
        }.getOrElse { "Login error: ${it.message}" }
    }

    suspend fun register(username: String, email: String, password: String): String {
        return runCatching {
            registerUseCase(username, email, password).message ?: ""
        }.getOrElse { "Register error: ${it.message}" }
    }

    fun logout() {
        sessionStorage.clearSession()
        _state.update { it.copy(isLoggedIn = false) }
    }
}
