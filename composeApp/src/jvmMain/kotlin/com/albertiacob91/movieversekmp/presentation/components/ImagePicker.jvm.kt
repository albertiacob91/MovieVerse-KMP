package com.albertiacob91.movieversekmp.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
actual fun rememberImagePickerLauncher(onImagePicked: (ByteArray) -> Unit): () -> Unit {
    val scope = rememberCoroutineScope()
    return {
        scope.launch(Dispatchers.IO) {
            val chooser = JFileChooser()
            chooser.fileFilter = FileNameExtensionFilter("Imágenes", "jpg", "jpeg", "png", "webp")
            val result = chooser.showOpenDialog(null)
            if (result == JFileChooser.APPROVE_OPTION) {
                val bytes = chooser.selectedFile.readBytes()
                withContext(Dispatchers.Main) { onImagePicked(bytes) }
            }
        }
    }
}
