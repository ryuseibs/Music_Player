package com.example.music_player.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.music_player.PlayerScreen
import com.example.music_player.AlbumScreen
import com.example.music_player.ArtistScreen
import com.example.music_player.PlaylistScreen

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Player.route,
        modifier = modifier
    ) {
        composable(Screen.Player.route) {
            PlayerScreen()
        }
        composable(Screen.AlbumList.route) {
            AlbumScreen()
        }
        composable(Screen.ArtistList.route) {
            ArtistScreen()
        }
        composable(Screen.Playlist.route) {
            PlaylistScreen()
        }
    }
}