package com.example.music_player

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.music_player.viewmodel.ArtistViewModel

@Composable
fun ArtistScreen(navController: NavController,
                 viewModel: ArtistViewModel = viewModel(),
                 context: Context = LocalContext.current
) {
    val artists by viewModel.artists.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadArtists(context)
    }

    LazyColumn {
        items(artists) { artist ->
            ListItem(
                headlineContent = { Text(artist.name) },
                modifier = Modifier
                    .clickable {
                        navController.navigate("albumListScreen/${Uri.encode(artist.name)}")
                    }
            )
        }
    }
}