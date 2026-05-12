package com.albertiacob91.movieversekmp.presentation.screens.movies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.albertiacob91.movieversekmp.presentation.components.MovieCard
import com.albertiacob91.movieversekmp.presentation.theme.Dimens
import com.albertiacob91.movieversekmp.presentation.theme.windowSizeClassFor
import com.albertiacob91.movieversekmp.presentation.viewmodel.MoviesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.layout.BoxWithConstraints

@Composable
fun MoviesScreen(
    contentPadding: PaddingValues,
    searchQuery: String,
    listState: LazyGridState,
    onMovieClick: (Int) -> Unit
) {
    val viewModel: MoviesViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(searchQuery) {
        viewModel.loadForQuery(searchQuery)
    }

    LaunchedEffect(listState, state.movies, state.isLoading, state.isLoadingMore, state.endReached, state.nextTriggerIndex) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }
            .map { it ?: -1 }
            .distinctUntilChanged()
            .collectLatest { lastVisibleItemIndex ->
                if (
                    lastVisibleItemIndex >= state.nextTriggerIndex &&
                    !state.isLoading &&
                    !state.isLoadingMore &&
                    !state.endReached
                ) {
                    viewModel.loadNextPage()
                }
            }
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
                    .padding(horizontal = Dimens.screenPadding)
            ) {
                when {
                    state.isLoading -> {
                        Text(
                            text = "Cargando películas...",
                            modifier = Modifier.padding(top = Dimens.mediumSpacing)
                        )
                    }

                    state.error.isNotBlank() && state.movies.isEmpty() -> {
                        Text(
                            text = "Error: ${state.error}",
                            modifier = Modifier.padding(top = Dimens.mediumSpacing)
                        )
                    }

                    state.movies.isEmpty() -> {
                        Text(
                            text = if (searchQuery.isBlank()) "No hay películas disponibles" else "No se encontraron resultados",
                            modifier = Modifier.padding(top = Dimens.mediumSpacing)
                        )
                    }

                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(columns),
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(Dimens.smallSpacing),
                            horizontalArrangement = Arrangement.spacedBy(Dimens.smallSpacing)
                        ) {
                            itemsIndexed(state.movies) { _, movie ->
                                MovieCard(
                                    movie = movie,
                                    onClick = { onMovieClick(movie.id) },
                                    gridMode = gridMode
                                )
                            }

                            if (state.isLoadingMore) {
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = Dimens.mediumSpacing),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        CircularProgressIndicator()
                                        Text(
                                            text = "Cargando más resultados...",
                                            modifier = Modifier.padding(top = 12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
