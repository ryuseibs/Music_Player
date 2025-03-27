package com.example.music_player

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun AlbumDetailScreen(
    albumId: Long,
    navController: NavController,
    context: Context = LocalContext.current,
    viewModel: MusicViewModel = viewModel()
    ) {
    val songs by viewModel.songsByAlbum.collectAsState()

    LaunchedEffect(albumId) {
        viewModel.loadSongsByAlbum(albumId)
    }

    LazyColumn {
        items(songs) {
             song ->
            ListItem(
                headlineContent = { Text(song.title) },
                supportingContent = { Text(song.artist) },
                modifier = Modifier.clickable {
                    val intent = Intent(context, PlayerActivity::class.java).apply {
                        putExtra("songId", song.id)
                    }
                    context.startActivity(intent)
                }
            )
        }
    }
}