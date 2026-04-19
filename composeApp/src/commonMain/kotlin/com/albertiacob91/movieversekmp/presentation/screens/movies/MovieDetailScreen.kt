package com.albertiacob91.movieversekmp.presentation.screens.movies

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.albertiacob91.movieversekmp.data.local.SessionStorage
import com.albertiacob91.movieversekmp.data.remote.AuthApi
import com.albertiacob91.movieversekmp.data.remote.CommentDto
import com.albertiacob91.movieversekmp.data.remote.MovieDto
import com.albertiacob91.movieversekmp.presentation.components.CommentItem
import com.albertiacob91.movieversekmp.presentation.theme.Dimens
import kotlinx.coroutines.launch

@Composable
fun MovieDetailScreen(
    movieId: Int,
    onBackClick: () -> Unit
) {
    val authApi = remember { AuthApi() }
    val sessionStorage = remember { SessionStorage() }
    val scope = rememberCoroutineScope()
    val context = LocalPlatformContext.current

    var movie by remember { mutableStateOf<MovieDto?>(null) }
    var comments by remember { mutableStateOf<List<CommentDto>>(emptyList()) }
    var commentText by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var favoriteMessage by remember { mutableStateOf("") }
    var commentMessage by remember { mutableStateOf("") }
    var isFavorite by remember { mutableStateOf(false) }

    suspend fun loadComments() {
        comments = authApi.getComments(movieId)
    }

    suspend fun loadFavoriteState() {
        val token = sessionStorage.getToken()
        isFavorite = if (!token.isNullOrBlank()) {
            authApi.isFavorite(token, movieId)
        } else {
            false
        }
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

        runCatching { loadComments() }
        runCatching { loadFavoriteState() }

        isLoading = false
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.screenPadding)
    ) {
        item {
            Button(onClick = onBackClick) {
                Text("Volver")
            }

            Spacer(modifier = Modifier.height(Dimens.mediumSpacing))
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
                    movie?.posterUrl?.let { posterUrl ->
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(posterUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = movie?.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(Dimens.posterDetailHeight),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(Dimens.mediumSpacing))
                    }

                    Text(
                        text = movie?.title.orEmpty(),
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(Dimens.smallSpacing))

                    Text(
                        text = movie?.releaseDate ?: "Sin fecha",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(Dimens.mediumSpacing))

                    Text(
                        text = movie?.overview?.ifBlank { "Sin descripción" } ?: "Sin descripción",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(Dimens.largeSpacing))

                    Button(
                        onClick = {
                            val token = sessionStorage.getToken()
                            if (token.isNullOrBlank()) {
                                favoriteMessage = "Sesión no válida"
                            } else {
                                scope.launch {
                                    favoriteMessage = "Procesando..."

                                    val success = if (isFavorite) {
                                        authApi.removeFavorite(token, movieId)
                                    } else {
                                        authApi.addFavorite(token, movieId)
                                    }

                                    if (success) {
                                        isFavorite = !isFavorite
                                        favoriteMessage = if (isFavorite) {
                                            "Añadida a favoritas"
                                        } else {
                                            "Quitada de favoritas"
                                        }
                                    } else {
                                        favoriteMessage = "No se pudo actualizar"
                                    }
                                }
                            }
                        }
                    ) {
                        Text(if (isFavorite) "Quitar de favoritas" else "Añadir a favoritas")
                    }

                    Spacer(modifier = Modifier.height(Dimens.smallSpacing))
                    Text(favoriteMessage)

                    Spacer(modifier = Modifier.height(Dimens.largeSpacing))

                    Text(
                        text = "Comentarios",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(Dimens.smallSpacing))

                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        label = { Text("Escribe un comentario") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(Dimens.smallSpacing))

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

                    Spacer(modifier = Modifier.height(Dimens.smallSpacing))
                    Text(commentMessage)

                    Spacer(modifier = Modifier.height(Dimens.mediumSpacing))
                }

                items(comments) { comment ->
                    CommentItem(comment)
                }
            }
        }
    }
}