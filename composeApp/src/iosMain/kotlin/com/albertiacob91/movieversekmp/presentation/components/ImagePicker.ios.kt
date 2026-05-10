package com.albertiacob91.movieversekmp.presentation.components

import androidx.compose.runtime.Composable

@Composable
actual fun rememberImagePickerLauncher(onImagePicked: (ByteArray) -> Unit): () -> Unit {
    return {}
}
