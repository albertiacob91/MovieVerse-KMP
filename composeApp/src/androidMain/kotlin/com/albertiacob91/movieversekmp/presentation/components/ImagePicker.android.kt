package com.albertiacob91.movieversekmp.presentation.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberImagePickerLauncher(onImagePicked: (ByteArray) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val bytes = context.contentResolver.openInputStream(it)?.use { stream -> stream.readBytes() }
            bytes?.let(onImagePicked)
        }
    }
    return { launcher.launch("image/*") }
}
