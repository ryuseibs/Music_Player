package com.example.music_player

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.music_player.MusicRepository.getEmbeddedAlbumArt
import com.example.music_player.ui.compomemts.BlurredArtworkBackground
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDetailScreen(
    albumId: Long,
    albumArtPathEncoded: String?,
    navController: NavController,
    context: Context = LocalContext.current,
    viewModel: MusicViewModel = viewModel()
    ) {
    val songs by viewModel.songsByAlbum.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val albumArtPath = Uri.decode(albumArtPathEncoded)
    Log.d("AlbumDetailScreen", "🚚 Decoded albumArtPath: $albumArtPath")

    LaunchedEffect(albumId) {
        viewModel.loadSongsByAlbum(albumId)
    }
    LaunchedEffect(Unit) {
        Log.d("DEBUG2", "🚚 albumArtPath received: $albumArtPath")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        BlurredArtworkBackground(albumArtPath = albumArtPath)

        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(modifier = Modifier.height(15.dp))

            AlbumInfoHeader(
                albumArtPath = albumArtPath,
                albumTitle = songs.firstOrNull()?.album ?: "",
                artist = songs.firstOrNull()?.artist ?:"",
                genreAndYear = "TODO"
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(songs) { song ->
                    Box(
                        modifier = Modifier
                            .clickable {
                                coroutineScope.launch {
                                    val albumSongs = MusicRepository.getSongsByAlbum(context, albumId)
                                    viewModel.setAlbumSongList(context, albumSongs, song.id)
                                    val intent = Intent(context, PlayerActivity::class.java).apply {
                                        putExtra("songId", song.id)
                                        putExtra("albumId", albumId)
                                    }
                                    context.startActivity(intent)
                                }
                            }
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier

                        ) {
                            Text(
                                text = song.title,
                                color = Color.Black
                            )
                            Text(
                                text = song.artist,
                                color = Color.LightGray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlbumInfoHeader(
    albumArtPath: String,
    albumTitle: String,
    artist: String,
    genreAndYear: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val artworkBitmap = remember(albumArtPath) {
            val file = File(albumArtPath)
            if (file.exists()) {
                BitmapFactory.decodeFile(file.path)?.asImageBitmap()
            } else null
        }

        artworkBitmap?.let {
            Image(
                bitmap = it,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(end = 16.dp)
            )
        }

        Column {
            Text(text = albumTitle, color = Color.Black)
            Text(text = artist, color = Color.DarkGray)
            Text(text = genreAndYear, color = Color.Gray)
        }
    }
}