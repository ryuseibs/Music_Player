package com.example.music_player.navigation

sealed class Screen (val route: String) {
    object Player: Screen("playerScreen")
    object AlbumList: Screen("albumListScreen")
    object ArtistList: Screen("artistListScreen")
    object Playlist: Screen("playlist")
}