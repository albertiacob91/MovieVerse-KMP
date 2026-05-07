package com.albertiacob91.movieversekmp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albertiacob91.movieversekmp.data.local.SessionStorage
import com.albertiacob91.movieversekmp.data.remote.MovieDto
import com.albertiacob91.movieversekmp.domain.usecase.movies.GetFavoritesUseCase
import com.albertiacob91.movieversekmp.domain.usecase.movies.GetMovieDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val movies: List<MovieDto> = emptyList(),
    val isLoading: Boolean = true,
    val error: String = ""
)

class FavoritesViewModel(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesUiState())
    val state: StateFlow<FavoritesUiState> = _state.asStateFlow()

    fun loadFavorites() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = "") }
            val token = sessionStorage.getToken()
            if (token.isNullOrBlank()) {
                _state.update { it.copy(error = "Sesión no válida", isLoading = false) }
                return@launch
            }
            runCatching {
                val favs = getFavoritesUseCase(token)
                favs.mapNotNull { getMovieDetailUseCase(it.movieId) }
            }.onSuccess { movies ->
                _state.update { it.copy(movies = movies, isLoading = false) }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message ?: "Error", isLoading = false) }
            }
        }
    }
}
