package com.albertiacob91.movieversekmp.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun TrailerPlayer(
    trailerUrl: String,
    modifier: Modifier = Modifier
)