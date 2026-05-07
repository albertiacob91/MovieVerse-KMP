package com.albertiacob91.movieversekmp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albertiacob91.movieversekmp.data.local.SessionStorage
import com.albertiacob91.movieversekmp.data.remote.ForumMessageDto
import com.albertiacob91.movieversekmp.domain.usecase.forum.CreateForumMessageUseCase
import com.albertiacob91.movieversekmp.domain.usecase.forum.GetForumMessagesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ForumChatDetailUiState(
    val messages: List<ForumMessageDto> = emptyList(),
    val isLoading: Boolean = true,
    val isSending: Boolean = false,
    val error: String = ""
)

class ForumChatDetailViewModel(
    private val getForumMessagesUseCase: GetForumMessagesUseCase,
    private val createForumMessageUseCase: CreateForumMessageUseCase,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private val _state = MutableStateFlow(ForumChatDetailUiState())
    val state: StateFlow<ForumChatDetailUiState> = _state.asStateFlow()

    fun loadMessages(chatId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = "") }
            runCatching { getForumMessagesUseCase(chatId) }
                .onSuccess { messages -> _state.update { it.copy(messages = messages, isLoading = false) } }
                .onFailure { e -> _state.update { it.copy(error = e.message ?: "Error cargando mensajes", isLoading = false) } }
        }
    }

    fun sendMessage(chatId: String, content: String) {
        if (content.isBlank()) return
        val token = sessionStorage.getToken()
        if (token.isNullOrBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isSending = true) }
            runCatching { createForumMessageUseCase(token, chatId, content.trim()) }
                .onSuccess { created ->
                    if (created != null) {
                        loadMessages(chatId)
                    } else {
                        _state.update { it.copy(error = "No se pudo enviar el mensaje", isSending = false) }
                    }
                }
                .onFailure { e -> _state.update { it.copy(error = e.message ?: "Error enviando mensaje", isSending = false) } }
            _state.update { it.copy(isSending = false) }
        }
    }
}
