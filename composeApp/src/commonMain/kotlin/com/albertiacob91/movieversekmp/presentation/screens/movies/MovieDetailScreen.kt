package com.albertiacob91.movieversekmp.presentation.screens.movies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.albertiacob91.movieversekmp.data.local.SessionStorage
import com.albertiacob91.movieversekmp.data.remote.AuthApi
import com.albertiacob91.movieversekmp.data.remote.MovieDto

@Composable
fun MovieDetailScreen(
    movieId: Int,
    onBackClick: () -> Unit
) {
    val authApi = remember { AuthApi() }
    val sessionStorage = remember { SessionStorage() }

    var movie by remember { mutableStateOf<MovieDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var favoriteMessage by remember { mutableStateOf("") }

    LaunchedEffect(movieId) {
        runCatching {
            authApi.getMovieDetail(movieId)
        }.onSuccess {
            movie = it
            errorMessage = if (it == null) "Película no encontrada" else ""
        }.onFailure {
            errorMessage = it.message ?: "Error cargando detalle"
        }
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Button(onClick = onBackClick) {
            Text("Volver")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                Text("Cargando detalle...")
            }

            errorMessage.isNotBlank() -> {
                Text("Error: $errorMessage")
            }

            movie != null -> {
                Text(movie!!.title)
                Spacer(modifier = Modifier.height(8.dp))
                Text(movie!!.releaseDate ?: "Sin fecha")
                Spacer(modifier = Modifier.height(8.dp))
                Text(movie!!.overview.ifBlank { "Sin descripción" })

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val token = sessionStorage.getToken()
                        if (token.isNullOrBlank()) {
                            favoriteMessage = "Sesión no válida"
                        } else {
                            favoriteMessage = "Procesando..."
                        }
                    }
                ) {
                    Text("Añadir a favoritas")
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(favoriteMessage)
            }
        }
    }

    LaunchedEffect(favoriteMessage) {
        if (favoriteMessage == "Procesando...") {
            val token = sessionStorage.getToken()
            if (!token.isNullOrBlank()) {
                val success = authApi.addFavorite(token, movieId)
                favoriteMessage = if (success) {
                    "Añadida a favoritas"
                } else {
                    "No se pudo añadir"
                }
            }
        }
    }
}