package com.albertiacob91.movieversekmp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.albertiacob91.movieversekmp.data.remote.MovieDto

@Composable
fun MovieCard(
    movie: MovieDto,
    onClick: () -> Unit
) {
    val context = LocalPlatformContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            movie.posterUrl?.let { posterUrl ->
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(posterUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = movie.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Text(
                text = movie.title,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(movie.releaseDate ?: "Sin fecha")
            Text(
                text = movie.overview.ifBlank { "Sin descripción" }.take(140),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}