package com.example.music_player.navigation

import android.util.Log
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
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

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
        composable(
            "playerScreen/{songId}"
        ) { backStackEntry ->
            val songId = backStackEntry.arguments?.getString("songId")?.toLongOrNull()
            songId?.let{
                PlayerScreen()
            }
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
        composable(
            "albumDetailScreen/{albumId}/{albumArtPath}",
            arguments = listOf(
                navArgument("albumId") { type = NavType.LongType },
                navArgument("albumArtPath") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val albumId = backStackEntry.arguments?.getLong("albumId") ?: return@composable
            val albumArtPathEncoded = backStackEntry.arguments?.getString("albumArtPath") ?: ""

            AlbumDetailScreen(
                albumId = albumId,
                albumArtPathEncoded = albumArtPathEncoded,
                navController = navController
            )
        }
        composable(Screen.Playlist.route) {
            PlaylistScreen()
        }
    }
}