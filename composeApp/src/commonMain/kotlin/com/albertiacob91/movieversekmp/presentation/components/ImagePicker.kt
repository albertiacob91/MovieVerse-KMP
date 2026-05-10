package com.albertiacob91.movieversekmp.presentation.components

import androidx.compose.runtime.Composable

@Composable
expect fun rememberImagePickerLauncher(onImagePicked: (ByteArray) -> Unit): () -> Unit
