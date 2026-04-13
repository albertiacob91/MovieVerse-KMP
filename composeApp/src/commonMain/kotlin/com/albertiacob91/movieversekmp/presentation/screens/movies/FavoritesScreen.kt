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
import com.albertiacob91.movieversekmp.data.local.SessionStorage
import com.albertiacob91.movieversekmp.data.remote.AuthApi
import com.albertiacob91.movieversekmp.data.remote.FavoriteDto

@Composable
fun FavoritesScreen(
    onBackClick: () -> Unit
) {
    val authApi = remember { AuthApi() }
    val sessionStorage = remember { SessionStorage() }

    var favorites by remember { mutableStateOf<List<FavoriteDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val token = sessionStorage.getToken()

        if (token.isNullOrBlank()) {
            errorMessage = "Sesión no válida"
            isLoading = false
            return@LaunchedEffect
        }

        runCatching {
            authApi.getFavorites(token)
        }.onSuccess {
            favorites = it
            errorMessage = ""
        }.onFailure {
            errorMessage = it.message ?: "Error cargando favoritas"
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

        when {
            isLoading -> Text("Cargando favoritas...")
            errorMessage.isNotBlank() -> Text("Error: $errorMessage")
            favorites.isEmpty() -> Text("No tienes favoritas todavía")
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp)
                ) {
                    items(favorites) { favorite ->
                        Text(
                            text = "Movie ID: ${favorite.movieId}",
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}