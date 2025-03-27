package com.example.music_player

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material3.ListItem
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.music_player.viewmodel.AlbumViewModel
import java.io.File
import coil.compose.rememberAsyncImagePainter

@Composable
fun AlbumScreen(
    artistName: String,
    navController: NavController,
    context: Context = LocalContext.current,
    viewModel: AlbumViewModel = viewModel()
) {
    val albums by viewModel.albums.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAlbumsByArtist(context, artistName)
    }

    LazyColumn {
        items(albums) { album ->
            ListItem(
                headlineContent = { Text(album.albumName) },
                supportingContent = {Text(album.artist)},
                leadingContent = {
                    album.albumArtPath?.let {
                        Image(
                            painter = rememberAsyncImagePainter(File(album.albumArtPath)),
                            contentDescription = null,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                },
                modifier = Modifier
                    .clickable {
                        navController.navigate("albumDetailScreen/${album.albumId}")
                    }

            )

        }
    }
}