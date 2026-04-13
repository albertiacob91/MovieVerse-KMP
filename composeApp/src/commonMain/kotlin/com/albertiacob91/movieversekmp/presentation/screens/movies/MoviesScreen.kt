package com.albertiacob91.movieversekmp.presentation.screens.movies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.albertiacob91.movieversekmp.data.remote.AuthApi
import com.albertiacob91.movieversekmp.data.remote.MovieDto
import com.albertiacob91.movieversekmp.presentation.components.MovieCard

@Composable
fun MoviesScreen(
    onLogoutClick: () -> Unit
) {
    val authApi = remember { AuthApi() }
    var movies by remember { mutableStateOf<List<MovieDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Button(onClick = onLogoutClick) {
            Text("Logout")
        }

        when {
            isLoading -> {
                Text("Cargando películas...")
            }

            errorMessage.isNotBlank() -> {
                Text("Error: $errorMessage")
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp)
                ) {
                    items(movies) { movie ->
                        MovieCard(movie)
                    }
                }
            }
        }
    }
}