package com.albertiacob91.movieversekmp.presentation.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WindowSizeClass { COMPACT, MEDIUM, EXPANDED }

fun windowSizeClassFor(width: Dp): WindowSizeClass = when {
    width < 600.dp -> WindowSizeClass.COMPACT
    width < 840.dp -> WindowSizeClass.MEDIUM
    else -> WindowSizeClass.EXPANDED
}
