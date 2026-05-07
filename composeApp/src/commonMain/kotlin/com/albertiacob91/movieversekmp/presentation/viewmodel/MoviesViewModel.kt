package com.albertiacob91.movieversekmp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.albertiacob91.movieversekmp.data.remote.MovieDto
import com.albertiacob91.movieversekmp.domain.usecase.movies.GetPopularMoviesUseCase
import com.albertiacob91.movieversekmp.domain.usecase.movies.SearchMoviesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MoviesUiState(
    val movies: List<MovieDto> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val error: String = "",
    val endReached: Boolean = false,
    val currentPage: Int = 0,
    val pageSize: Int = 20,
    val nextTriggerIndex: Int = 20,
    val currentQuery: String = ""
)

class MoviesViewModel(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MoviesUiState())
    val state: StateFlow<MoviesUiState> = _state.asStateFlow()

    fun loadForQuery(query: String) {
        val current = _state.value
        if (current.isLoading && current.currentQuery == query) return
        viewModelScope.launch {
            runCatching { loadInitialPages(query) }
                .onFailure { e ->
                    _state.update { it.copy(error = e.message ?: "Error cargando películas", isLoading = false) }
                }
        }
    }

    private suspend fun loadInitialPages(query: String) {
        _state.update {
            it.copy(
                isLoading = true, isLoadingMore = false, error = "",
                endReached = false, currentPage = 0, nextTriggerIndex = 20, currentQuery = query
            )
        }

        val firstPage = fetchPage(query, 1)
        val pageSize = if (firstPage.isNotEmpty()) firstPage.size else 20

        if (firstPage.isEmpty()) {
            _state.update { it.copy(movies = emptyList(), currentPage = 1, endReached = true, isLoading = false, pageSize = pageSize) }
            return
        }

        val secondPage = fetchPage(query, 2)
        _state.update {
            it.copy(
                movies = firstPage + secondPage,
                currentPage = if (secondPage.isNotEmpty()) 2 else 1,
                endReached = secondPage.isEmpty(),
                nextTriggerIndex = pageSize,
                isLoading = false,
                pageSize = pageSize
            )
        }
    }

    fun loadNextPage() {
        val current = _state.value
        if (current.isLoading || current.isLoadingMore || current.endReached) return
        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true, error = "") }
            runCatching {
                val pageToLoad = _state.value.currentPage + 1
                val newMovies = fetchPage(_state.value.currentQuery, pageToLoad)
                if (newMovies.isEmpty()) {
                    _state.update { it.copy(endReached = true, isLoadingMore = false) }
                } else {
                    _state.update {
                        it.copy(
                            movies = it.movies + newMovies,
                            currentPage = pageToLoad,
                            nextTriggerIndex = it.nextTriggerIndex + it.pageSize,
                            isLoadingMore = false
                        )
                    }
                }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message ?: "Error cargando más películas", isLoadingMore = false) }
            }
        }
    }

    private suspend fun fetchPage(query: String, page: Int): List<MovieDto> =
        if (query.isBlank()) getPopularMoviesUseCase(page) else searchMoviesUseCase(query, page)
}
