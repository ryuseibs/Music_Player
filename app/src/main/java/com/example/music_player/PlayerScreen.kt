package com.example.music_player

import android.graphics.BitmapFactory
import android.util.Log
import android.view.Surface
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.Surface
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File

@Composable
fun PlayerScreen(viewModel: MusicViewModel = viewModel()) {
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val context = LocalContext.current
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val albumArt = currentSong.albumArtPath?.let { path ->
                Log.d("PlayerScreen", "Album Art Path: $path")
                val file = File(path)
                if (file.exists()) {
                    BitmapFactory.decodeFile(path)!!.asImageBitmap()
                } else {
                    Log.d("PlayerScreen", "Album Art File Not Found: $path")
                    null
                }
            } ?: BitmapFactory.decodeResource(context.resources, R.drawable.placeholder_artwork).asImageBitmap()

            Image(
                bitmap = albumArt,
                contentDescription = "Album Artwork",
                modifier = Modifier
                    .size(250.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, Color.Gray, RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = currentSong.title,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = currentSong.artist,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(24.dp))

            Slider(
                value = currentPosition.toFloat(),
                valueRange = 0f..duration.toFloat(),
                onValueChange = { viewModel.seekTo(it.toInt()) },
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = formatTime(currentPosition))
                Text(text = formatTime(duration))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button( onClick = { viewModel.previousTrack(context)}) {
                    Text("前の曲")
                }

                Button(
                    onClick = {
                        if (isPlaying) viewModel.pause() else viewModel.playCurrentTrack(context)
                    }
                ) {
                    Text(if (isPlaying) "一時停止" else "再生")
                }

                Button(onClick = { viewModel.nextTrack(context)}) {
                    Text("次の曲")
                }
            }
        }
    }
}

fun formatTime(milliseconds: Int): String {
    val minutes = milliseconds / 1000 / 60
    val seconds = milliseconds / 1000 % 60
    return "%02d:%02d".format(minutes, seconds)
}