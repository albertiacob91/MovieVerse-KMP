package com.albertiacob91.movieversekmp.presentation.screens.movies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.albertiacob91.movieversekmp.presentation.components.MovieCard
import com.albertiacob91.movieversekmp.presentation.theme.Dimens
import com.albertiacob91.movieversekmp.presentation.viewmodel.MoviesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MoviesScreen(
    contentPadding: PaddingValues,
    searchQuery: String,
    listState: LazyListState,
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
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
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(Dimens.smallSpacing)
                ) {
                    itemsIndexed(state.movies) { _, movie ->
                        MovieCard(
                            movie = movie,
                            onClick = { onMovieClick(movie.id) }
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
