package com.example.music_player

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import android.media.MediaMetadataRetriever
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object MusicRepository {

    fun getMusicList(context: Context): List<Song> {
        val songList = mutableListOf<Song>()
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA
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
            val albumColumn = it.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val dataColumn = it.getColumnIndex(MediaStore.Audio.Media.DATA)

            while (it.moveToNext()) {
                val title = it.getString(titleColumn)
                val artist = it.getString(artistColumn)
                val album = it.getString(albumColumn)
                val data = it.getString(dataColumn)

                songList.add(Song(title, artist, album, data, null)) // `data` はファイルパス
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            songList.forEachIndexed { index, song ->
                val albumArtPath = getEmbeddedAlbumArt(context, song.filePath)
                songList[index] = song.copy(albumArtPath = albumArtPath)
            }
        }

        return songList
    }

    private fun getEmbeddedAlbumArt(context: Context, filePath: String): String? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(filePath)
            val art = retriever.embeddedPicture
            retriever.release()

            if (art != null) {
                val file = File(context.cacheDir, "album_art_${filePath.hashCode()}.jpg")
                FileOutputStream(file).use { fos ->
                    fos.write(art)
                }
                Log.d("MusicRepository", "Saved Album Art: ${file.absolutePath}") // 🔹 ログで確認！
                file.absolutePath
            } else {
                Log.d("MusicRepository", "No Embedded Album Art Found: $filePath") // 🔹 ログで確認！
                null
            }
        } catch (e: Exception) {
            Log.e("MusicRepository", "Error retrieving album art: ${e.message}")
            retriever.release()
            null
        }
    }
}