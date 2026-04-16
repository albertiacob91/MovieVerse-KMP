package com.albertiacob91.movieversekmp.presentation.screens.movies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.albertiacob91.movieversekmp.data.remote.AuthApi
import com.albertiacob91.movieversekmp.data.remote.MovieDto
import com.albertiacob91.movieversekmp.presentation.components.MovieCard
import kotlinx.coroutines.launch

@Composable
fun MoviesScreen(
    onLogoutClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onFavoritesClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val authApi = remember { AuthApi() }
    val scope = rememberCoroutineScope()

    var movies by remember { mutableStateOf<List<MovieDto>>(emptyList()) }
    var isLoading by rememberSaveable { mutableStateOf(true) }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var hasLoadedInitially by rememberSaveable { mutableStateOf(false) }

    fun loadPopularMovies() {
        scope.launch {
            isLoading = true
            runCatching {
                authApi.getPopularMovies()
            }.onSuccess {
                movies = it
                errorMessage = ""
            }.onFailure {
                errorMessage = it.message ?: "Error cargando películas"
            }
            isLoading = false
        }
    }

    fun searchMovies() {
        scope.launch {
            isLoading = true
            runCatching {
                authApi.searchMovies(searchQuery)
            }.onSuccess {
                movies = it
                errorMessage = ""
            }.onFailure {
                errorMessage = it.message ?: "Error buscando películas"
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        if (!hasLoadedInitially) {
            loadPopularMovies()
            hasLoadedInitially = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Button(onClick = onLogoutClick) {
            Text("Logout")
        }

        Button(
            onClick = onFavoritesClick,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Ver favoritas")
        }

        Button(
            onClick = onProfileClick,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Ver perfil")
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar películas") },
            modifier = Modifier.padding(top = 12.dp)
        )

        Button(
            onClick = {
                if (searchQuery.isBlank()) {
                    loadPopularMovies()
                } else {
                    searchMovies()
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Buscar")
        }

        when {
            isLoading -> {
                Text("Cargando películas...", modifier = Modifier.padding(top = 16.dp))
            }

            errorMessage.isNotBlank() -> {
                Text("Error: $errorMessage", modifier = Modifier.padding(top = 16.dp))
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp)
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