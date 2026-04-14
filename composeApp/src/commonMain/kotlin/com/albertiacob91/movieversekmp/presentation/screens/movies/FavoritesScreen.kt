package com.albertiacob91.movieversekmp.presentation.screens.movies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.albertiacob91.movieversekmp.data.local.SessionStorage
import com.albertiacob91.movieversekmp.data.remote.AuthApi
import com.albertiacob91.movieversekmp.presentation.model.FavoriteMovieUi
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(
    onBackClick: () -> Unit,
    onMovieClick: (Int) -> Unit
) {
    val authApi = remember { AuthApi() }
    val sessionStorage = remember { SessionStorage() }
    val scope = rememberCoroutineScope()

    var favorites by remember { mutableStateOf<List<FavoriteMovieUi>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    suspend fun loadFavorites() {
        val token = sessionStorage.getToken()

        if (token.isNullOrBlank()) {
            errorMessage = "Sesión no válida"
            isLoading = false
            return
        }

        val favoriteDtos = authApi.getFavorites(token)

        val mapped = favoriteDtos.mapNotNull { favorite ->
            authApi.getMovieDetail(favorite.movieId)?.let { movie ->
                FavoriteMovieUi(
                    movieId = favorite.movieId,
                    title = movie.title,
                    releaseDate = movie.releaseDate
                )
            }
        }

        favorites = mapped
        errorMessage = ""
        isLoading = false
    }

    LaunchedEffect(Unit) {
        loadFavorites()
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
                        Card(
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(favorite.title)
                                Text(favorite.releaseDate ?: "Sin fecha")

                                Button(
                                    onClick = { onMovieClick(favorite.movieId) },
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text("Ver detalle")
                                }

                                Button(
                                    onClick = {
                                        val token = sessionStorage.getToken()
                                        if (!token.isNullOrBlank()) {
                                            scope.launch {
                                                val removed = authApi.removeFavorite(token, favorite.movieId)
                                                if (removed) {
                                                    loadFavorites()
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text("Quitar de favoritas")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}