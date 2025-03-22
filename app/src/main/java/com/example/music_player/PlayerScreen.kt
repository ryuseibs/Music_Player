package com.example.music_player

import android.content.Context
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.util.Log
import android.view.Surface
import android.widget.ImageButton
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import java.io.File
import androidx.constraintlayout.compose.*
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import kotlinx.coroutines.delay
import android.graphics.Bitmap
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex

fun getArtworkBitmapFromPath(path: String?): Bitmap? {
    return path?.let {
        BitmapFactory.decodeFile(it)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(viewModel: MusicViewModel = viewModel()) {
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val context = LocalContext.current
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val remainingTime by viewModel.remainingTime.collectAsState()
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

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            bitmap = albumArt,
            contentDescription = "Reflected Artwork",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(465.dp)
                .align(Alignment.BottomCenter)
                .clip(RectangleShape)
                .graphicsLayer {
                    scaleY = -1f
                    alpha = 0.4f
                }
                .blur(25.dp)
        )

        Surface(
            color = Color.Transparent,
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(PaddingValues(0.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                viewModel.dominantColor.value.copy(alpha = 0.2f)
                            )
                        )
                    )
            )
            LaunchedEffect(currentSong) {
                currentSong?.let { song ->
                    val bitmap = getArtworkBitmapFromPath(song.albumArtPath)
                    bitmap?.let {
                        viewModel.updateArtworkColor(it)
                    }
                }
            }

            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(0.dp),
            ) {
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

                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(1f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(currentPosition),
                        fontSize = 15.sp,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Text(text = "-" + formatTime(remainingTime),
                        fontSize = 15.sp,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(31.dp))

                @Composable
                fun AnimateScrollText(
                    text: String,
                    modifier: Modifier = Modifier,
                    fontSize: TextUnit = 24.sp,
                    speed: Int = 10000
                ) {
                    val scrollState = rememberScrollState()
                    var textWidth by remember { mutableStateOf(0) }
                    var containerWidth by remember { mutableStateOf(0) }
                    val canScroll = remember(textWidth, containerWidth) {
                        textWidth > containerWidth
                    }

                    LaunchedEffect(canScroll) {
                        if (canScroll) {
                            val scrollDistance = textWidth - containerWidth

                            while (true) {
                                scrollState.animateScrollTo(
                                    scrollDistance,
                                    animationSpec = tween(
                                        durationMillis = speed,
                                        easing = LinearEasing
                                    )
                                )
                                delay(1000)
                                scrollState.scrollTo(0)
                                delay(500)
                            }
                        }
                    }

                    Box(
                        modifier = modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .wrapContentSize(Alignment.Center)
                            .horizontalScroll(scrollState, enabled = false)
                    ) {
                        Text(
                            text = text,
                            fontSize = fontSize,
                            fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily.SansSerif,
                            color = Color.Black,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Clip,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned {
                                    textWidth = it.size.width
                                }
                        )
                    }
                }

                AnimateScrollText(text = currentSong.title)

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = currentSong.artist + "  -  " + currentSong.album,
                    fontSize = 17.sp,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.Gray,
                    overflow = TextOverflow.Clip,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(80.dp))

                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    val (controlButtons) = createRefs()

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(horizontal = 32.dp)
                            .constrainAs(controlButtons) {
                                bottom.linkTo(parent.bottom, margin = 180.dp)
                            }
                    ) {
                        IconButton(
                            onClick = { viewModel.previousTrack(context) },
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.Transparent)
                                .offset(y = 0.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.previous),
                                contentDescription = "Previous",
                                modifier = Modifier
                                    .size(35.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                if (isPlaying) viewModel.pause() else viewModel.playCurrentTrack(context)
                            },
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.Transparent)
                        ) {
                            if (isPlaying) {
                                Image(
                                    painter = painterResource(id = R.drawable.pause),
                                    contentDescription = "Pause",
                                    modifier = Modifier
                                        .size(35.dp)
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.baseline_play_arrow_24),
                                    contentDescription = "Pause",
                                    modifier = Modifier
                                        .size(55.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.nextTrack(context) },
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RectangleShape)
                                .background(Color.Transparent)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.next),
                                contentDescription = "Next",
                                modifier = Modifier
                                    .size(35.dp)
                            )
                        }
                    }
                }
                @Composable
                fun VolumeControl(viewModel: MusicViewModel) {
                    var volume by remember { mutableStateOf(1f) }
                    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    val maxVolume = remember { audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) }
                    val currentVolume = remember { mutableStateOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)) }

                    ConstraintLayout(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        val (volumeSlider) = createRefs()
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .constrainAs(volumeSlider) {
                                    bottom.linkTo(parent.bottom, margin = 120.dp)
                                }
                                .padding(horizontal = 32.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Slider(
                                value = currentVolume.value.toFloat(),
                                onValueChange = {
                                    val newVolume = it.toInt().coerceIn(0,maxVolume)
                                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0)
                                    currentVolume.value = newVolume
                                },
                                valueRange = 0f..maxVolume.toFloat(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = SliderDefaults.colors(
                                    thumbColor = Color.White,
                                    activeTrackColor = Color.Black,
                                    inactiveTrackColor = Color.Gray
                                )
                            )
                        }
                    }
                }
                VolumeControl(viewModel)
            }
        }
    }
}

fun formatTime(milliseconds: Int): String {
    val minutes = milliseconds / 1000 / 60
    val seconds = milliseconds / 1000 % 60
    return "%02d:%02d".format(minutes, seconds)
}
