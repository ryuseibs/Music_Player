package com.example.music_player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.music_player.navigation.Screen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ArtistScreen(navController: NavController) {

    val artistList = listOf("Perfume", "Dua Lipa" , "きゃりーぱみゅぱみゅ")

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "アーティスト一覧",
            modifier = Modifier
                .padding(16.dp),
            fontSize = 24.sp
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(artistList) {
               artist ->
                Text(
                    text = artist,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate(Screen.AlbumList.createRoute(artist))
                        }
                        .padding(16.dp)
                )
            }
        }
    }
}