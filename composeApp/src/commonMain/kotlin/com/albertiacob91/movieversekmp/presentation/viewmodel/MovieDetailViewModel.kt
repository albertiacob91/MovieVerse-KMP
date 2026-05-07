package com.albertiacob91.movieversekmp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albertiacob91.movieversekmp.data.local.SessionStorage
import com.albertiacob91.movieversekmp.data.remote.CommentDto
import com.albertiacob91.movieversekmp.data.remote.MovieDto
import com.albertiacob91.movieversekmp.domain.usecase.movies.CheckIsFavoriteUseCase
import com.albertiacob91.movieversekmp.domain.usecase.movies.GetCommentsUseCase
import com.albertiacob91.movieversekmp.domain.usecase.movies.GetMovieDetailUseCase
import com.albertiacob91.movieversekmp.domain.usecase.movies.PostCommentUseCase
import com.albertiacob91.movieversekmp.domain.usecase.movies.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MovieDetailUiState(
    val movie: MovieDto? = null,
    val comments: List<CommentDto> = emptyList(),
    val isFavorite: Boolean = false,
    val isLoading: Boolean = true,
    val error: String = "",
    val favoriteMessage: String = "",
    val commentMessage: String = ""
)

class MovieDetailViewModel(
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val checkIsFavoriteUseCase: CheckIsFavoriteUseCase,
    private val postCommentUseCase: PostCommentUseCase,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private val _state = MutableStateFlow(MovieDetailUiState())
    val state: StateFlow<MovieDetailUiState> = _state.asStateFlow()

    fun load(movieId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = "") }

            runCatching { getMovieDetailUseCase(movieId) }
                .onSuccess { movie ->
                    _state.update { it.copy(movie = movie, error = if (movie == null) "Película no encontrada" else "") }
                }
                .onFailure { e ->
                    _state.update { it.copy(error = e.message ?: "Error cargando detalle") }
                }

            runCatching { getCommentsUseCase(movieId) }
                .onSuccess { comments -> _state.update { it.copy(comments = comments) } }

            val token = sessionStorage.getToken()
            if (!token.isNullOrBlank()) {
                runCatching { checkIsFavoriteUseCase(token, movieId) }
                    .onSuccess { isFav -> _state.update { it.copy(isFavorite = isFav) } }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    fun toggleFavorite(movieId: Int) {
        val token = sessionStorage.getToken()
        if (token.isNullOrBlank()) {
            _state.update { it.copy(favoriteMessage = "Sesión no válida") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(favoriteMessage = "Procesando...") }
            val currentlyFavorite = _state.value.isFavorite
            runCatching { toggleFavoriteUseCase(token, movieId, currentlyFavorite) }
                .onSuccess { success ->
                    if (success) {
                        val nowFavorite = !currentlyFavorite
                        _state.update {
                            it.copy(
                                isFavorite = nowFavorite,
                                favoriteMessage = if (nowFavorite) "Añadida a favoritas" else "Quitada de favoritas"
                            )
                        }
                    } else {
                        _state.update { it.copy(favoriteMessage = "No se pudo actualizar") }
                    }
                }
                .onFailure { e -> _state.update { it.copy(favoriteMessage = "Error: ${e.message}") } }
        }
    }

    fun postComment(movieId: Int, content: String) {
        val token = sessionStorage.getToken()
        when {
            token.isNullOrBlank() -> _state.update { it.copy(commentMessage = "Sesión no válida") }
            content.isBlank() -> _state.update { it.copy(commentMessage = "El comentario no puede estar vacío") }
            else -> {
                viewModelScope.launch {
                    _state.update { it.copy(commentMessage = "Enviando...") }
                    runCatching { postCommentUseCase(token, movieId, content) }
                        .onSuccess { success ->
                            if (success) {
                                val updated = runCatching { getCommentsUseCase(movieId) }.getOrDefault(emptyList())
                                _state.update { it.copy(comments = updated, commentMessage = "Comentario publicado") }
                            } else {
                                _state.update { it.copy(commentMessage = "No se pudo publicar") }
                            }
                        }
                        .onFailure { e -> _state.update { it.copy(commentMessage = "Error: ${e.message}") } }
                }
            }
        }
    }
}
