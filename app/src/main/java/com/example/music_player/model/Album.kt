package com.example.music_player.model

data class Album(
    val albumId: Long,
    val albumName: String,
    val artist: String,
    val albumYear: Int,
    val albumSongCount: Int,
    val albumArtPath: String? = null
)