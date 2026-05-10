package com.albertiacob91.movieversekmp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albertiacob91.movieversekmp.domain.usecase.auth.GetCurrentUserUseCase
import com.albertiacob91.movieversekmp.domain.usecase.auth.UploadAvatarUseCase
import com.albertiacob91.movieversekmp.presentation.model.ProfileUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

data class ProfileUiState(
    val profile: ProfileUi? = null,
    val isLoading: Boolean = true,
    val isUploadingAvatar: Boolean = false,
    val error: String = ""
)

class ProfileViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val uploadAvatarUseCase: UploadAvatarUseCase
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
                                profile = ProfileUi(
                                    id = me.id,
                                    username = me.username,
                                    email = me.email,
                                    avatarUrl = me.avatarUrl
                                ),
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

    @OptIn(ExperimentalEncodingApi::class)
    fun uploadAvatar(imageBytes: ByteArray) {
        viewModelScope.launch {
            _state.update { it.copy(isUploadingAvatar = true) }
            val base64 = Base64.encode(imageBytes)
            runCatching { uploadAvatarUseCase(base64) }
                .onSuccess { ok ->
                    if (ok) loadProfile()
                    else _state.update { it.copy(isUploadingAvatar = false, error = "No se pudo subir la foto") }
                }
                .onFailure { e -> _state.update { it.copy(isUploadingAvatar = false, error = e.message ?: "Error") } }
            _state.update { it.copy(isUploadingAvatar = false) }
        }
    }
}
