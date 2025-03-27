package com.example.music_player.model

data class Album(
    val albumId: Long,
    val albumName: String,
    val artist: String,
    val albumArtPath: String? = null
)