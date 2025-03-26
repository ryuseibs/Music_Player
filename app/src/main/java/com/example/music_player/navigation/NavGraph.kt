package com.example.music_player.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.music_player.PlayerScreen
import com.example.music_player.AlbumScreen
import com.example.music_player.AlbumDetailScreen
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
        composable(
            route = Screen.AlbumDetailScreen.route,
            arguments = listOf(navArgument("albumId") {
                type = NavType.StringType
            })
        ) {
          backStackEntry ->
            val albumId = backStackEntry.arguments?.getString("albumId") ?: ""
            AlbumDetailScreen(albumId)
        }
        composable(Screen.ArtistList.route) {
            ArtistScreen()
        }
        composable(Screen.Playlist.route) {
            PlaylistScreen()
        }
    }
}