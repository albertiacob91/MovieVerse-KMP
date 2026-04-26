package com.albertiacob91.movieversekmp.presentation.screens.movies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

    var movies by remember { mutableStateOf<List<MovieDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    suspend fun loadPopularMovies() {
        isLoading = true
        errorMessage = ""
        movies = authApi.getPopularMovies()
        isLoading = false
    }

    suspend fun searchMovies(query: String) {
        isLoading = true
        errorMessage = ""
        movies = authApi.searchMovies(query)
        isLoading = false
    }

    LaunchedEffect(searchQuery) {
        runCatching {
            if (searchQuery.isBlank()) {
                loadPopularMovies()
            } else {
                searchMovies(searchQuery)
            }
        }.onFailure {
            errorMessage = it.message ?: "Error cargando películas"
            isLoading = false
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

            errorMessage.isNotBlank() -> {
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
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(Dimens.smallSpacing)
                ) {
                    items(movies) { movie ->
                        MovieCard(
                            movie = movie,
                            onClick = { onMovieClick(movie.id) }
                        )
                    }
                }
            }
        }
    }
}