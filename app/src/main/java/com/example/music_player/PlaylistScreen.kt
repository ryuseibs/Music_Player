package com.example.music_player

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun PlaylistScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            "Playlist Screen",
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}