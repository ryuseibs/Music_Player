package com.example.music_player.navigation

sealed class Screen (val route: String) {
    object ArtistList: Screen("artistListScreen")
    object Player: Screen("playerScreen")
    object AlbumList: Screen("albumListScreen/{artistName}") {
        fun createRoute(artistName: String): String = "albumListScreen/$artistName"
    }
    object AlbumDetailScreen: Screen("albumDetailScreen/{albumId}"){
        fun createRoute(albumId: String): String = "albumDetailScreen/$albumId"
    }
    object Playlist: Screen("playlist")
}