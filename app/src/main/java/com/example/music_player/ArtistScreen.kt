package com.example.music_player

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.music_player.viewmodel.ArtistViewModel
import kotlinx.coroutines.launch
import java.net.URLEncoder

@Composable
fun ArtistScreen(

    navController: NavController,
    viewModel: ArtistViewModel = viewModel(),
    viewModel2: MusicViewModel = viewModel(),
    context: Context = LocalContext.current
) {
    val artists by viewModel.artists.collectAsState()
    val recentAlbums by viewModel2.recentAlbums.collectAsState()
    val allsongs by viewModel2.songList.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadArtists(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column()
        {
            Text(
                text = "最近追加した項目",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray,
                modifier = Modifier
                    .padding(start = 16.dp, bottom = 8.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(recentAlbums) { album ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(album.albumArtPath),
                            contentDescription = album.albumName,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(8.dp))
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
                        Text(
                            text = album.albumName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .width(160.dp)
                        )
                        Text(
                            text = album.artist,
                            fontSize = 11.sp,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            LazyColumn(
                modifier = Modifier
                    .background(Color.White)
            ) {
                items(artists) { artist ->
                    val artistSong = allsongs.filter { it.artist == artist.name }
                    val albumCount = artistSong.map { it.albumId }.distinct().count()
                    val artworkPath = artistSong.firstOrNull {it.albumArtPath != null}?.albumArtPath
                    ListItem(
                        leadingContent = {
                            if (artworkPath != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(artworkPath),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                )
                            }
                        },
                        headlineContent = { Text(
                            text = artist.name,
                            color = Color.Black
                        ) },
                        supportingContent = { Text(
                            text = "${albumCount}アルバム",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                        },
                        modifier = Modifier
                            .clickable {
                                navController.navigate("albumListScreen/${Uri.encode(artist.name)}")
                            }
                    )
                }
            }
        }
    }
}