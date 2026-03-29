package com.albertiacob91.movieversekmp

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.albertiacob91.movieversekmp.presentation.MovieVerseApp
import com.albertiacob91.movieversekmp.presentation.theme.MovieVerseTheme

@Composable
fun App() {
    MovieVerseTheme {
        Surface {
            MovieVerseApp()
        }
    }
}