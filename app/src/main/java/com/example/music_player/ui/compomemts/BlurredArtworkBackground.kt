package com.example.music_player.ui.compomemts

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import java.io.File

@Composable
fun BlurredArtworkBackground(albumArtPath: String?) {
    val context = LocalContext.current
    val artworkBitmap = remember(albumArtPath) {
        albumArtPath?.let {
            val file = File(it)
            Log.d("DEBUG2", "📦 Checking file exists: ${file.absolutePath}")
            if (file.exists()) {
                Log.d("DEBUG2", "Bitmap decode success")
                BitmapFactory.decodeFile(it)?.asImageBitmap()
            } else {
                Log.e("DEBUG2", "File not found: ${file.absolutePath}")
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
                .alpha(1f)
        )
    } ?: Box (
        modifier = Modifier
            .fillMaxSize()
    )
}