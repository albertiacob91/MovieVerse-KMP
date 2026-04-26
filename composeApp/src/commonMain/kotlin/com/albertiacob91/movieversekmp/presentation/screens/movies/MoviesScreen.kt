package com.albertiacob91.movieversekmp.presentation.screens.movies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.albertiacob91.movieversekmp.data.remote.AuthApi
import com.albertiacob91.movieversekmp.data.remote.MovieDto
import com.albertiacob91.movieversekmp.presentation.components.MovieCard
import com.albertiacob91.movieversekmp.presentation.theme.Dimens
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Composable
fun MoviesScreen(
    contentPadding: PaddingValues,
    searchQuery: String,
    listState: LazyListState,
    onMovieClick: (Int) -> Unit
) {
    val authApi = remember { AuthApi() }

    var movies by remember { mutableStateOf<List<MovieDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isLoadingMore by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    var currentPage by remember { mutableStateOf(0) }
    var pageSize by remember { mutableStateOf(20) }
    var endReached by remember { mutableStateOf(false) }
    var nextTriggerIndex by remember { mutableStateOf(20) }

    suspend fun fetchPage(page: Int): List<MovieDto> {
        return if (searchQuery.isBlank()) {
            authApi.getPopularMovies(page = page)
        } else {
            authApi.searchMovies(searchQuery, page = page)
        }
    }

    suspend fun loadInitialPages() {
        isLoading = true
        isLoadingMore = false
        errorMessage = ""
        endReached = false
        currentPage = 0
        nextTriggerIndex = 20

        val firstPage = fetchPage(1)
        pageSize = if (firstPage.isNotEmpty()) firstPage.size else 20

        if (firstPage.isEmpty()) {
            movies = emptyList()
            currentPage = 1
            endReached = true
            isLoading = false
            return
        }

        val secondPage = fetchPage(2)

        movies = firstPage + secondPage
        currentPage = if (secondPage.isNotEmpty()) 2 else 1
        endReached = secondPage.isEmpty()

        nextTriggerIndex = pageSize
        isLoading = false
    }

    suspend fun loadNextPage() {
        if (isLoading || isLoadingMore || endReached) return

        isLoadingMore = true
        errorMessage = ""

        val pageToLoad = currentPage + 1
        val newMovies = fetchPage(pageToLoad)

        if (newMovies.isEmpty()) {
            endReached = true
        } else {
            movies = movies + newMovies
            currentPage = pageToLoad
            nextTriggerIndex += pageSize
        }

        isLoadingMore = false
    }

    LaunchedEffect(searchQuery) {
        runCatching {
            loadInitialPages()
        }.onFailure {
            errorMessage = it.message ?: "Error cargando películas"
            isLoading = false
            isLoadingMore = false
        }
    }

    LaunchedEffect(listState, movies, isLoading, isLoadingMore, endReached, nextTriggerIndex) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }
            .map { it ?: -1 }
            .distinctUntilChanged()
            .collectLatest { lastVisibleItemIndex ->
                if (
                    lastVisibleItemIndex >= nextTriggerIndex &&
                    !isLoading &&
                    !isLoadingMore &&
                    !endReached
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
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = Dimens.mediumSpacing),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()

                                Text(
                                    text = "Cargando más resultados...",
                                    modifier = Modifier.padding(top = 12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}