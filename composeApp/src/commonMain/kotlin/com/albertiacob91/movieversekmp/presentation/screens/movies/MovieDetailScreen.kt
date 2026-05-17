package com.albertiacob91.movieversekmp.presentation.screens.movies

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.albertiacob91.movieversekmp.presentation.components.CastItem
import com.albertiacob91.movieversekmp.presentation.components.CommentItem
import com.albertiacob91.movieversekmp.presentation.components.TrailerPlayer
import com.albertiacob91.movieversekmp.presentation.theme.Dimens
import com.albertiacob91.movieversekmp.presentation.theme.GoldStar
import com.albertiacob91.movieversekmp.presentation.viewmodel.MovieDetailViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MovieDetailScreen(
    movieId: Int,
    onBackClick: () -> Unit
) {
    val viewModel: MovieDetailViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    var commentText by remember { mutableStateOf("") }
    val context = LocalPlatformContext.current

    LaunchedEffect(movieId) {
        viewModel.load(movieId)
    }

    LaunchedEffect(state.commentMessage) {
        if (state.commentMessage == "Comentario publicado") {
            commentText = ""
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val mediaHeight = (maxWidth * 0.5625f).coerceIn(200.dp, 450.dp)

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            LazyColumn(modifier = Modifier.fillMaxSize().widthIn(max = Dimens.maxContentWidth)) {
                item {
                    DetailTopBar(
                        title = state.movie?.title ?: "",
                        isFavorite = state.isFavorite,
                        onBackClick = onBackClick,
                        onFavoriteClick = { viewModel.toggleFavorite(movieId) }
                    )
                }

                when {
                    state.isLoading -> {
                        item {
                            Text(
                                text = "Cargando detalle...",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    state.error.isNotBlank() -> {
                        item {
                            Text(
                                text = "Error: ${state.error}",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    state.movie != null -> {
                        item {
                            val currentMovie = state.movie!!

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
                                            .height(mediaHeight)
                                            .clip(MaterialTheme.shapes.large)
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
                                                .height(mediaHeight)
                                                .clip(MaterialTheme.shapes.large),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.height(18.dp))
                                    }
                                }

                                Text(
                                    text = currentMovie.title,
                                    style = MaterialTheme.typography.headlineSmall
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    val ratingText = currentMovie.voteAverage?.let {
                                        val rounded = ((it * 10).toInt() / 10.0)
                                        if (rounded % 1.0 == 0.0) rounded.toInt().toString() else rounded.toString()
                                    } ?: "-"
                                    val yearText = currentMovie.releaseDate?.takeIf { it.length >= 4 }?.substring(0, 4) ?: "----"
                                    val runtimeText = currentMovie.runtime?.let { "$it min" } ?: "--"

                                    DetailInfoPill(
                                        icon = {
                                            Icon(
                                                Icons.Default.Star,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp),
                                                tint = GoldStar
                                            )
                                        },
                                        text = ratingText
                                    )
                                    DetailInfoPill(
                                        icon = {
                                            Icon(
                                                Icons.Default.CalendarToday,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        },
                                        text = yearText
                                    )
                                    DetailInfoPill(
                                        icon = {
                                            Icon(
                                                Icons.Default.Schedule,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        },
                                        text = runtimeText
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    val genres = currentMovie.genres.takeIf { it.isNotEmpty() } ?: listOf("Película")
                                    genres.take(5).forEach { genre -> GenrePill(genre) }
                                }

                                if (currentMovie.watchProviders.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text(
                                        text = "Dónde ver",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    val streaming = currentMovie.watchProviders.filter { it.type == "streaming" }
                                    val rent = currentMovie.watchProviders.filter { it.type == "rent" }
                                    val buy = currentMovie.watchProviders.filter { it.type == "buy" }
                                    if (streaming.isNotEmpty()) {
                                        WatchProviderRow(label = "En streaming", providers = streaming)
                                        Spacer(modifier = Modifier.height(10.dp))
                                    }
                                    if (rent.isNotEmpty()) {
                                        WatchProviderRow(label = "Alquiler", providers = rent)
                                        Spacer(modifier = Modifier.height(10.dp))
                                    }
                                    if (buy.isNotEmpty()) {
                                        WatchProviderRow(label = "Compra", providers = buy)
                                    }
                                }

                                if (currentMovie.cast.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text(
                                        text = "Reparto principal",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        items(currentMovie.cast) { castMember -> CastItem(castMember) }
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Text(text = "Sinopsis", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = currentMovie.overview.ifBlank { "Sin descripción" },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                if (state.favoriteMessage.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = state.favoriteMessage,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Spacer(modifier = Modifier.height(28.dp))

                                Text(text = "Comentarios", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = commentText,
                                    onValueChange = { commentText = it },
                                    label = { Text("Escribe un comentario") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(onClick = { viewModel.postComment(movieId, commentText) }) {
                                    Text("Publicar comentario")
                                }

                                if (state.commentMessage.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = state.commentMessage,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        items(state.comments.size) { index ->
                            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                                CommentItem(state.comments[index])
                            }
                        }

                        item { Spacer(modifier = Modifier.height(24.dp)) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailTopBar(
    title: String,
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver"
                )
            }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}

@Composable
private fun DetailInfoPill(icon: @Composable () -> Unit, text: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun GenrePill(text: String) {
    Box(
        modifier = Modifier
            .wrapContentHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun WatchProviderRow(label: String, providers: List<com.albertiacob91.movieversekmp.data.remote.WatchProviderDto>) {
    Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(modifier = Modifier.height(6.dp))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(providers) { provider -> WatchProviderItem(provider) }
    }
}

@Composable
private fun WatchProviderItem(provider: com.albertiacob91.movieversekmp.data.remote.WatchProviderDto) {
    val context = LocalPlatformContext.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (provider.logoUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(provider.logoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = provider.name,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = provider.name,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 56.dp)
        )
    }
}
