package com.albertiacob91.movieversekmp.presentation.screens.movies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.albertiacob91.movieversekmp.data.remote.AuthApi
import com.albertiacob91.movieversekmp.data.remote.MovieDto
import com.albertiacob91.movieversekmp.presentation.components.MovieCard
import com.albertiacob91.movieversekmp.presentation.theme.Dimens

@Composable
fun MoviesScreen(
    contentPadding: PaddingValues,
    searchQuery: String,
    onMovieClick: (Int) -> Unit
) {
    val authApi = remember { AuthApi() }
    val listState = rememberLazyListState()

    var movies by remember { mutableStateOf<List<MovieDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isLoadingMore by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var currentPage by remember { mutableStateOf(1) }
    var endReached by remember { mutableStateOf(false) }

    suspend fun loadFirstPage() {
        isLoading = true
        isLoadingMore = false
        errorMessage = ""
        currentPage = 1
        endReached = false

        val firstPage = if (searchQuery.isBlank()) {
            authApi.getPopularMovies(page = 1)
        } else {
            authApi.searchMovies(searchQuery, page = 1)
        }

        movies = firstPage
        endReached = firstPage.isEmpty()
        isLoading = false
    }

    suspend fun loadNextPage() {
        if (isLoading || isLoadingMore || endReached) return

        isLoadingMore = true
        errorMessage = ""

        val nextPage = currentPage + 1

        val newMovies = if (searchQuery.isBlank()) {
            authApi.getPopularMovies(page = nextPage)
        } else {
            authApi.searchMovies(searchQuery, page = nextPage)
        }

        if (newMovies.isEmpty()) {
            endReached = true
        } else {
            movies = movies + newMovies
            currentPage = nextPage
        }

        isLoadingMore = false
    }

    LaunchedEffect(searchQuery) {
        runCatching {
            loadFirstPage()
        }.onFailure {
            errorMessage = it.message ?: "Error cargando películas"
            isLoading = false
            isLoadingMore = false
        }
    }

    LaunchedEffect(listState, movies, isLoading, isLoadingMore, endReached) {
        snapshotFlow {
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            lastVisibleItemIndex to totalItemsCount
        }.collect { (lastVisibleItemIndex, totalItemsCount) ->
            if (
                lastVisibleItemIndex != null &&
                totalItemsCount > 0 &&
                lastVisibleItemIndex >= totalItemsCount - 3
            ) {
                runCatching {
                    loadNextPage()
                }.onFailure {
                    errorMessage = it.message ?: "Error cargando más películas"
                    isLoadingMore = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = Dimens.screenPadding)
    ) {
        when {
            isLoading -> {
                Text(
                    text = "Cargando películas...",
                    modifier = Modifier.padding(top = Dimens.mediumSpacing)
                )
            }

            errorMessage.isNotBlank() && movies.isEmpty() -> {
                Text(
                    text = "Error: $errorMessage",
                    modifier = Modifier.padding(top = Dimens.mediumSpacing)
                )
            }

            movies.isEmpty() -> {
                Text(
                    text = if (searchQuery.isBlank()) {
                        "No hay películas disponibles"
                    } else {
                        "No se encontraron resultados"
                    },
                    modifier = Modifier.padding(top = Dimens.mediumSpacing)
                )
            }

            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(Dimens.smallSpacing)
                ) {
                    itemsIndexed(movies) { _, movie ->
                        MovieCard(
                            movie = movie,
                            onClick = { onMovieClick(movie.id) }
                        )
                    }

                    if (isLoadingMore) {
                        item {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(vertical = Dimens.mediumSpacing)
                            )
                        }
                    }
                }
            }
        }
    }
}