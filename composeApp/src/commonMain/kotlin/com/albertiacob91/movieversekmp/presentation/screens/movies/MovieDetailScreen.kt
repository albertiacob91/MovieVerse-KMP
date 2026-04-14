package com.albertiacob91.movieversekmp.presentation.screens.movies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.albertiacob91.movieversekmp.data.local.SessionStorage
import com.albertiacob91.movieversekmp.data.remote.AuthApi
import com.albertiacob91.movieversekmp.data.remote.CommentDto
import com.albertiacob91.movieversekmp.data.remote.MovieDto
import com.albertiacob91.movieversekmp.presentation.components.CommentItem
import kotlinx.coroutines.launch

@Composable
fun MovieDetailScreen(
    movieId: Int,
    onBackClick: () -> Unit
) {
    val authApi = remember { AuthApi() }
    val sessionStorage = remember { SessionStorage() }
    val scope = rememberCoroutineScope()

    var movie by remember { mutableStateOf<MovieDto?>(null) }
    var comments by remember { mutableStateOf<List<CommentDto>>(emptyList()) }
    var commentText by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var favoriteMessage by remember { mutableStateOf("") }
    var commentMessage by remember { mutableStateOf("") }

    suspend fun loadComments() {
        comments = authApi.getComments(movieId)
    }

    LaunchedEffect(movieId) {
        runCatching {
            authApi.getMovieDetail(movieId)
        }.onSuccess {
            movie = it
            errorMessage = if (it == null) "Película no encontrada" else ""
        }.onFailure {
            errorMessage = it.message ?: "Error cargando detalle"
        }

        runCatching {
            loadComments()
        }

        isLoading = false
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Button(onClick = onBackClick) {
                Text("Volver")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        when {
            isLoading -> {
                item { Text("Cargando detalle...") }
            }

            errorMessage.isNotBlank() -> {
                item { Text("Error: $errorMessage") }
            }

            movie != null -> {
                item {
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

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Comentarios")

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        label = { Text("Escribe un comentario") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val token = sessionStorage.getToken()
                            when {
                                token.isNullOrBlank() -> {
                                    commentMessage = "Sesión no válida"
                                }
                                commentText.isBlank() -> {
                                    commentMessage = "El comentario no puede estar vacío"
                                }
                                else -> {
                                    scope.launch {
                                        commentMessage = "Enviando..."

                                        val success = authApi.addComment(token, movieId, commentText)

                                        if (success) {
                                            commentText = ""
                                            commentMessage = "Comentario publicado"
                                            comments = authApi.getComments(movieId)
                                        } else {
                                            commentMessage = "No se pudo publicar"
                                        }
                                    }
                                }
                            }
                        }
                    ) {
                        Text("Publicar comentario")
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(commentMessage)

                    Spacer(modifier = Modifier.height(16.dp))
                }

                items(comments) { comment ->
                    CommentItem(comment)
                }
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