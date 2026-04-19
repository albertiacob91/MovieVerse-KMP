package com.albertiacob91.movieversekmp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

@Composable
fun MovieCard(
    movie: MovieDto,
    onClick: () -> Unit
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

    val runtimeText = movie.runtime?.toString() ?: "--"

    val genresToShow = if (movie.genres.isNotEmpty()) {
        movie.genres.take(3)
    } else {
        listOf("Película")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
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
                        .width(108.dp)
                        .height(156.dp)
                        .clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop
                )
            } ?: Box(
                modifier = Modifier
                    .width(108.dp)
                    .height(156.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(modifier = Modifier.width(18.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = movie.title.uppercase(),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Normal
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SmallInfoPill("★ $ratingText")
                    SmallInfoPill(year)
                    SmallInfoPill(runtimeText)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    genresToShow.forEach { genre ->
                        GenrePill(genre)
                    }
                }
            }
        }
    }
}

@Composable
private fun SmallInfoPill(text: String) {
    Box(
        modifier = Modifier
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

@Composable
private fun GenrePill(text: String) {
    Box(
        modifier = Modifier
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