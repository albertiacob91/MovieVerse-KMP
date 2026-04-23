package com.albertiacob91.movieversekmp.presentation.components

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
actual fun TrailerPlayer(
    trailerUrl: String,
    modifier: Modifier
) {
    val videoId = remember(trailerUrl) {
        trailerUrl
            .substringAfter("v=", "")
            .substringBefore("&")
            .substringBefore("?")
            .ifBlank { trailerUrl.substringAfterLast("/") }
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            YouTubePlayerView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                val options = IFramePlayerOptions.Builder(context)
                    .controls(1)
                    .rel(0)
                    .fullscreen(1)
                    .build()

                enableAutomaticInitialization = false

                initialize(
                    object : AbstractYouTubePlayerListener() {
                        override fun onReady(
                            youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
                        ) {
                            youTubePlayer.loadVideo(videoId, 0f)
                        }
                    },
                    true,
                    options
                )
            }
        }
    )
}