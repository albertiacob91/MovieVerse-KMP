package com.albertiacob91.movieversekmp

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.albertiacob91.movieversekmp.presentation.MovieVerseApp

@Composable
fun App() {
    MaterialTheme {
        Surface {
            MovieVerseApp()
        }
    }
}