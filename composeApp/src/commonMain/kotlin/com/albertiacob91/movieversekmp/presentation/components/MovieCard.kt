package com.albertiacob91.movieversekmp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.albertiacob91.movieversekmp.data.remote.MovieDto
import com.albertiacob91.movieversekmp.presentation.theme.GoldStar

@Composable
fun MovieCard(
    movie: MovieDto,
    onClick: () -> Unit,
    gridMode: Boolean = false
) {
    val context = LocalPlatformContext.current

    val year = movie.releaseDate
        ?.takeIf { it.length >= 4 }
        ?.substring(0, 4)
        ?: "----"

    val ratingText = movie.voteAverage?.let {
        val rounded = ((it * 10).toInt() / 10.0)
        if (rounded % 1.0 == 0.0) rounded.toInt().toString() else rounded.toString()
    } ?: "-"

    val genresToShow = if (movie.genres.isNotEmpty()) movie.genres.take(3) else listOf("Película")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        if (gridMode) {
            Column {
                movie.posterUrl?.let { posterUrl ->
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(posterUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = movie.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(2f / 3f)
                            .clip(MaterialTheme.shapes.extraLarge),
                        contentScale = ContentScale.Crop
                    )
                } ?: Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f / 3f)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .background(MaterialTheme.colorScheme.surface)
                )
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RatingBadge(ratingText)
                        InfoPill(year)
                        movie.runtime?.let { InfoPill("${it}m") }
                    }
                    if (movie.genres.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            movie.genres.take(2).forEach { genre -> GenrePill(genre) }
                        }
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                movie.posterUrl?.let { posterUrl ->
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(posterUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = movie.title,
                        modifier = Modifier
                            .width(96.dp)
                            .height(144.dp)
                            .clip(MaterialTheme.shapes.large),
                        contentScale = ContentScale.Crop
                    )
                } ?: Box(
                    modifier = Modifier
                        .width(96.dp)
                        .height(144.dp)
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.surface)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RatingBadge(ratingText)
                        InfoPill(year)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        genresToShow.forEach { genre -> GenrePill(genre) }
                    }
                }
            }
        }
    }
}

@Composable
private fun RatingBadge(rating: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = GoldStar,
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = rating,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun InfoPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun GenrePill(text: String) {
    Box(
        modifier = Modifier
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
