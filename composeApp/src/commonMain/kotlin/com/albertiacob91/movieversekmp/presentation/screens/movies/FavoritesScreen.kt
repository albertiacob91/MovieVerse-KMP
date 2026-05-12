package com.albertiacob91.movieversekmp.presentation.screens.movies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.albertiacob91.movieversekmp.presentation.components.MovieCard
import com.albertiacob91.movieversekmp.presentation.theme.Dimens
import com.albertiacob91.movieversekmp.presentation.theme.windowSizeClassFor
import com.albertiacob91.movieversekmp.presentation.viewmodel.FavoritesViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FavoritesScreen(
    contentPadding: PaddingValues,
    onMovieClick: (Int) -> Unit
) {
    val viewModel: FavoritesViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        val isLandscapeMobile = maxWidth > maxHeight && maxWidth < 900.dp
        val sizeClass = windowSizeClassFor(maxWidth)
        val columns = if (isLandscapeMobile) 4 else Dimens.gridColumns(sizeClass)
        val gridMode = columns > 1

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier
                    .widthIn(max = Dimens.maxContentWidth)
                    .fillMaxSize()
                    .padding(horizontal = Dimens.screenPadding),
                verticalArrangement = Arrangement.Top
            ) {
                when {
                    state.isLoading -> {
                        Text(text = "Cargando favoritas...", modifier = Modifier.padding(top = Dimens.mediumSpacing))
                    }

                    state.error.isNotBlank() -> {
                        Text(text = "Error: ${state.error}", modifier = Modifier.padding(top = Dimens.mediumSpacing))
                    }

                    state.movies.isEmpty() -> {
                        Text(text = "No tienes favoritas todavía", modifier = Modifier.padding(top = Dimens.mediumSpacing))
                    }

                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(columns),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = Dimens.mediumSpacing),
                            verticalArrangement = Arrangement.spacedBy(Dimens.smallSpacing),
                            horizontalArrangement = Arrangement.spacedBy(Dimens.smallSpacing)
                        ) {
                            items(state.movies) { movie ->
                                MovieCard(
                                    movie = movie,
                                    onClick = { onMovieClick(movie.id) },
                                    gridMode = gridMode
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
