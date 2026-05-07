package com.albertiacob91.movieversekmp

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.albertiacob91.movieversekmp.di.appModules
import com.albertiacob91.movieversekmp.presentation.MovieVerseApp
import com.albertiacob91.movieversekmp.presentation.theme.MovieVerseTheme
import org.koin.compose.KoinApplication

@Composable
fun App() {
    KoinApplication(application = {
        modules(appModules)
    }) {
        MovieVerseTheme {
            Surface {
                MovieVerseApp()
            }
        }
    }
}
