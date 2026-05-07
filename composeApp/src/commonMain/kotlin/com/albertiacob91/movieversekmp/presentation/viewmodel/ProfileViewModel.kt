package com.albertiacob91.movieversekmp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albertiacob91.movieversekmp.domain.usecase.auth.GetCurrentUserUseCase
import com.albertiacob91.movieversekmp.presentation.model.ProfileUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val profile: ProfileUi? = null,
    val isLoading: Boolean = true,
    val error: String = ""
)

class ProfileViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = "") }
            runCatching { getCurrentUserUseCase() }
                .onSuccess { me ->
                    if (me != null) {
                        _state.update {
                            it.copy(
                                profile = ProfileUi(id = me.id, username = me.username, email = me.email),
                                isLoading = false
                            )
                        }
                    } else {
                        _state.update { it.copy(error = "No se pudo cargar el perfil", isLoading = false) }
                    }
                }
                .onFailure { e -> _state.update { it.copy(error = e.message ?: "Error", isLoading = false) } }
        }
    }
}
