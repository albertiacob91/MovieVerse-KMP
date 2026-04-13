package com.albertiacob91.movieversekmp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.albertiacob91.movieversekmp.data.remote.MovieDto

@Composable
fun MovieCard(
    movie: MovieDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(movie.title)
            Text(movie.releaseDate ?: "Sin fecha")
            Text(
                movie.overview.ifBlank { "Sin descripción" },
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}