package com.example.music_player.ui.compomemts

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun BlurredArtworkBackground(albumArtPath: String?) {
    val context = LocalContext.current
    val artworkBitmap = remember(albumArtPath) {
        albumArtPath?.let {
            val file = File(it)
            if (file.exists()) {
                BitmapFactory.decodeFile(it)?.asImageBitmap()
            } else {
                null
            }
        }
    }

    artworkBitmap?.let {
        Image(
            bitmap = it,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(40.dp)
        )
    } ?: Box (
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0E0E0))
    )
}