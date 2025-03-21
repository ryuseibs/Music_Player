package com.example.music_player

import android.graphics.BitmapFactory
import android.util.Log
import android.view.Surface
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.mandatorySystemGestures
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import java.io.File
import androidx.constraintlayout.compose.*
import androidx.constraintlayout.widget.ConstraintLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(viewModel: MusicViewModel = viewModel()) {
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val context = LocalContext.current
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
            .offset(y = (-19).dp) //TODO: これがなくなると画面最上部に空白が生じる。調査は開発完了後とする(動的にしておく)。
            .consumeWindowInsets(PaddingValues(0.dp)),
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(0.dp),
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
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
                    .clip(RectangleShape)
            )

//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 0.dp)
//        ) {
//            Slider(
//                value = currentPosition.toFloat(),
//                valueRange = 0f..duration.toFloat(),
//                onValueChange = { viewModel.seekTo(it.toInt()) },
//                modifier = Modifier
//                    .width(LocalConfiguration.current.screenWidthDp.dp + 0.dp)
//                    .padding(horizontal = 0.dp), //TODO: シークバー設置後画面左右両端との余白が生じないようにする
//                colors = SliderDefaults.colors(
//                    thumbColor = Color.Transparent,
//                    activeTrackColor = Color.Black,
//                    inactiveTrackColor = Color.Gray
//                    //TODO: 再生後の進捗バーが完全透明になるようにする
//                    // （未再生時のバーの背景色がデフォルトで指定されているため再生しながら進捗バーを順次完全透明にすることが困難）
//                ),
//                thumb = {
//                    Box(
//                        modifier = Modifier
//                            .size(4.dp, 32.dp)
//                            .offset(x = 8.dp)
//                            .offset(y = 14.dp) // TODO: 今後動的にアートワークの直下に配置できるように調整予定
//                            .background(
//                                Color.Red,
//                                shape = RoundedCornerShape(0.dp, 0.dp, 5.dp, 5.dp)
//                            )
//                            .border(
//                                2.dp,
//                                Color.Red,
//                                shape = RoundedCornerShape(0.dp, 0.dp, 5.dp, 5.dp)
//                            )
//                    )
//                }
//            )
//        }

            @Composable
            fun CustomSlider(
                currentPosition: Float,
                duration: Float,
                onSeek: (Float) -> Unit,
                modifier: Modifier = Modifier
            ) {
                val progress = if (duration > 0) (currentPosition / duration).coerceIn(0f, 1f) else 0f
                //CanvasによるSlider実装
                Canvas(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val newProgress = (offset.x / size.width).coerceIn(0f, 1f)
                                onSeek(newProgress * duration)
                                println("Tapped at: ${offset.x}")
                            }
                        }
                ) {
                    val barHeight = 8.dp.toPx()
                    val barY = size.height / 2

                    //未再生バーのレイアウト
                    drawLine(
                        color = Color.Transparent.copy(alpha = 0.2f),
                        start = Offset(0f,barY),
                        end = Offset(size.width,barY),
                        strokeWidth = barHeight
                    )

                    //再生済(進捗)バーのレイアウト
                    drawLine(
                        color = Color.Black,
                        start = Offset(0f, barY),
                        end = Offset(size.width * progress, barY),
                        strokeWidth = barHeight
                    )

                    drawRoundRect(
                        color = Color.Red,
                        topLeft = Offset(size.width * progress - 4.dp.toPx(), barY - 4.dp.toPx()),
                        size = Size(4.dp.toPx(),32.dp.toPx()),
                        cornerRadius = CornerRadius(0.dp.toPx(),5.dp.toPx())
                        // TODO: ４箇所のうち下側の左右だけ丸みをつけたい（上記ではX,Y毎に丸めるため４箇所とも丸まってしまう）
                    )
                }
            }

            CustomSlider(
                currentPosition = currentPosition.toFloat(),
                duration = duration.toFloat(),
                onSeek = { newPosition -> println("Seek to: $newPosition") },
                modifier = Modifier.padding(0.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = formatTime(currentPosition))
                Text(text = formatTime(duration))
            }

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
