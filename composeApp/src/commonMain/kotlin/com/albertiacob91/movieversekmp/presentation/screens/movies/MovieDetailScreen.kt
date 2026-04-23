package com.albertiacob91.movieversekmp.presentation.screens.movies

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.albertiacob91.movieversekmp.data.local.SessionStorage
import com.albertiacob91.movieversekmp.data.remote.AuthApi
import com.albertiacob91.movieversekmp.data.remote.CommentDto
import com.albertiacob91.movieversekmp.data.remote.MovieDto
import com.albertiacob91.movieversekmp.presentation.components.CastItem
import com.albertiacob91.movieversekmp.presentation.components.CommentItem
import com.albertiacob91.movieversekmp.presentation.components.TrailerPlayer
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
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            DetailTopBar(
                title = movie?.title ?: "",
                isFavorite = isFavorite,
                onBackClick = onBackClick,
                onFavoriteClick = {
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
            )
        }

        when {
            isLoading -> {
                item {
                    Text(
                        text = "Cargando detalle...",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            errorMessage.isNotBlank() -> {
                item {
                    Text(
                        text = "Error: $errorMessage",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            movie != null -> {
                item {
                    val currentMovie = movie!!

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        if (!currentMovie.trailerUrl.isNullOrBlank()) {
                            TrailerPlayer(
                                trailerUrl = currentMovie.trailerUrl,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(400.dp)
                                    .clip(RoundedCornerShape(20.dp))
                            )

                            Spacer(modifier = Modifier.height(18.dp))
                        } else {
                            currentMovie.posterUrl?.let { posterUrl ->
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(posterUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = currentMovie.title,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(400.dp)
                                        .clip(RoundedCornerShape(20.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.height(18.dp))
                            }
                        }

                        Text(
                            text = currentMovie.title.uppercase(),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Normal
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val ratingText = currentMovie.voteAverage?.let {
                                val rounded = ((it * 10).toInt() / 10.0)
                                if (rounded % 1.0 == 0.0) rounded.toInt().toString() else rounded.toString()
                            } ?: "-"

                            val yearText = currentMovie.releaseDate
                                ?.takeIf { it.length >= 4 }
                                ?.substring(0, 4)
                                ?: "----"

                            val runtimeText = currentMovie.runtime?.let { "$it min" } ?: "--"

                            DetailInfoPill(
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                text = ratingText
                            )

                            DetailInfoPill(
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                text = yearText
                            )

                            DetailInfoPill(
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                text = runtimeText
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (currentMovie.genres.isNotEmpty()) {
                                currentMovie.genres.take(5).forEach { genre ->
                                    GenrePill(genre)
                                }
                            } else {
                                GenrePill("Película")
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        if (currentMovie.cast.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Main cast",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(currentMovie.cast) { castMember ->
                                    CastItem(castMember)
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                        } else {
                            Spacer(modifier = Modifier.height(18.dp))
                        }

                        if (favoriteMessage.isNotBlank()) {
                            Text(
                                text = favoriteMessage,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Text(
                            text = "Synopsis",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = currentMovie.overview.ifBlank { "Sin descripción" },
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        Text(
                            text = "Comentarios",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            label = { Text("Escribe un comentario") },
                            modifier = Modifier.fillMaxWidth()
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

                        if (commentMessage.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(commentMessage)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                items(comments.size) { index ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        CommentItem(comments[index])
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun DetailTopBar(
    title: String,
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(horizontal = 8.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White
            )
        }

        Text(
            text = title.uppercase(),
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = onFavoriteClick) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Favorito",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun DetailInfoPill(
    icon: @Composable () -> Unit,
    text: String
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun GenrePill(text: String) {
    Box(
        modifier = Modifier
            .wrapContentHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium
        )
    }
}