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
        startDestination = Screen.ArtistList.route,
        modifier = modifier
    ) {
        composable(Screen.ArtistList.route) {
            ArtistScreen(navController = navController)
        }
        composable(Screen.Player.route) {
            PlayerScreen()
        }
        composable(
            route = Screen.AlbumList.route,
            arguments = listOf(navArgument("artistName") {
                type = NavType.StringType
            })
        ) {
            backStackEntry ->
            val artistName = backStackEntry.arguments?.getString("artistName") ?: ""
            AlbumScreen(artistName = artistName, navController = navController)
        }
        composable("albumDetailScreen/{albumId}") {
          backStackEntry ->
            val albumId = backStackEntry.arguments?.getString("albumId") ?.toLongOrNull()
            albumId?.let {
                AlbumDetailScreen(albumId = it, navController = navController)
            }
        }
        composable(Screen.Playlist.route) {
            PlaylistScreen()
        }
    }
}