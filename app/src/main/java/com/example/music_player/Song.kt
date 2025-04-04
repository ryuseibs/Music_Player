package com.example.music_player

import java.time.Year

data class Song (
    val id: Long,               // 曲の一意
    val title: String,          // タイトル
    val artist: String,         // アーティスト
    val album: String,          // アルバム
    val albumId: Long,          // アルバムID
    val filePath: String,       // ファイルパス
    val albumArtPath: String?,  // アルバムアートワーク
    val trackNumber: Int,        // トラック番号
    val year: Int
)