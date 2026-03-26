package com.albertiacob91.movieversekmp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "MovieVerseKMP",
    ) {
        App()
    }
}