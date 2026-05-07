package com.albertiacob91.movieversekmp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albertiacob91.movieversekmp.data.local.SessionStorage
import com.albertiacob91.movieversekmp.data.remote.ForumChatDto
import com.albertiacob91.movieversekmp.domain.usecase.forum.CreateForumChatUseCase
import com.albertiacob91.movieversekmp.domain.usecase.forum.GetForumChatsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ForumUiState(
    val chats: List<ForumChatDto> = emptyList(),
    val isLoading: Boolean = true,
    val error: String = ""
)

class ForumViewModel(
    private val getForumChatsUseCase: GetForumChatsUseCase,
    private val createForumChatUseCase: CreateForumChatUseCase,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private val _state = MutableStateFlow(ForumUiState())
    val state: StateFlow<ForumUiState> = _state.asStateFlow()

    fun loadChats() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = "") }
            runCatching { getForumChatsUseCase() }
                .onSuccess { chats -> _state.update { it.copy(chats = chats, isLoading = false) } }
                .onFailure { e -> _state.update { it.copy(error = e.message ?: "Error cargando chats", isLoading = false) } }
        }
    }

    fun createChat(title: String) {
        val token = sessionStorage.getToken()
        if (token.isNullOrBlank()) {
            _state.update { it.copy(error = "Sesión no válida") }
            return
        }
        viewModelScope.launch {
            runCatching { createForumChatUseCase(token, title) }
                .onSuccess { created ->
                    if (created != null) loadChats()
                    else _state.update { it.copy(error = "No se pudo crear el chat") }
                }
                .onFailure { e -> _state.update { it.copy(error = e.message ?: "Error") } }
        }
    }
}
