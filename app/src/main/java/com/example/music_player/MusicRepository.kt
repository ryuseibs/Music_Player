package com.example.music_player

import android.content.Context
import android.provider.MediaStore

object MusicRepository {

    fun getMusicList(context: Context): List<Song> {
        val songList = mutableListOf<Song>()
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA // 音楽ファイルのパス
        )

        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            MediaStore.Audio.Media.IS_MUSIC + "!= 0",
            null,
            MediaStore.Audio.Media.TITLE + " ASC"
        )

        cursor?.use {
            val titleColumn = it.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artistColumn = it.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val dataColumn = it.getColumnIndex(MediaStore.Audio.Media.DATA)

            while (it.moveToNext()) {
                val title = it.getString(titleColumn)
                val artist = it.getString(artistColumn)
                val data = it.getString(dataColumn)

                songList.add(Song(title, artist, data)) // `data` はファイルパス
            }
        }

        return songList
    }
}