package com.example.music_player

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material3.ListItem
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.music_player.viewmodel.AlbumViewModel
import java.io.File
import coil.compose.rememberAsyncImagePainter
import com.example.music_player.model.Album
import com.example.music_player.ui.compomemts.BlurredArtworkBackground
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.net.URLEncoder

@Composable
fun AlbumScreen(
    artistName: String,
    navController: NavController,
    context: Context = LocalContext.current,
    viewModel: AlbumViewModel = viewModel(),
    viewModel2: MusicViewModel = viewModel()
) {
    val albums by viewModel.albums.collectAsState()
    val songs by viewModel2.songList.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val artworkPath = remember(songs) {
        songs
            .filter { it.artist ==artistName }
            .firstOrNull { it.albumArtPath != null}
            ?.albumArtPath
    }

    LaunchedEffect(Unit) {
        viewModel.loadAlbumsByArtist(context, artistName)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        BlurredArtworkBackground(albumArtPath = artworkPath)

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = artistName,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 16.dp, top = 250.dp, bottom = 8.dp)
            )

            LazyColumn {
                items(albums) { album ->
                    ListItem(
                        headlineContent = { Text(text = album.albumName, color = Color.Black) },
                        supportingContent = {Text("${album.albumYear}・${album.albumSongCount}曲")},
                        leadingContent = {
                            album.albumArtPath?.let {
                                Image(
                                    painter = rememberAsyncImagePainter(File(album.albumArtPath)),
                                    contentDescription = null,
                                    modifier = Modifier.size(56.dp)
                                )
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .clickable {
                                coroutineScope.launch {
                                    val songs = MusicRepository.getSongsByAlbum(context, album.albumId)
                                    val firstSongPath = songs.firstOrNull()?.filePath

                                    // 🔍 ログで確認
                                    android.util.Log.d("DEBUG2", "🎵 First song path: $firstSongPath [albumId=${album.albumId}]")

                                    val artworkPath = if (firstSongPath != null) {
                                        MusicRepository.getEmbeddedAlbumArt(context, firstSongPath) ?: ""
                                    } else {
                                        ""
                                    }

                                    if (artworkPath.isNotEmpty()) {
                                        val encodedPath = URLEncoder.encode(artworkPath, "UTF-8")
                                        Log.d("DEBUG2", "🎨 Navigating to albumDetailScreen with path: $artworkPath")
                                        navController.navigate("albumDetailScreen/${album.albumId}/${Uri.encode(artworkPath)}")
                                    } else {
                                        Log.e("DEBUG2", "❌ No artworkPath found. Skipping navigation.")
                                    }
                                }
                            }
                    )
                    Divider(
                        color = Color.White.copy(alpha = 0.2f),
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}