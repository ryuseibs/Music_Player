package com.example.music_player

data class Song (
    val title: String,          // タイトル
    val artist: String,         // アーティスト
    val album: String,          // アルバム
    val filePath: String,       // ファイルパス
    val albumArtPath: String?   // アルバムアートワーク
)