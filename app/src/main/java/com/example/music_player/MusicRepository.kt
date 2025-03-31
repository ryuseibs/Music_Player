package com.example.music_player

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.example.music_player.model.Artist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object MusicRepository {

    fun getMusicList(context: Context): List<Song> {
        val songList = mutableListOf<Song>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TRACK
        )

        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            MediaStore.Audio.Media.IS_MUSIC + "!= 0",
            null,
            MediaStore.Audio.Media.TITLE + " ASC"
        )

        cursor?.use {
            val idColumn = it.getColumnIndex(MediaStore.Audio.Media._ID)
            val titleColumn = it.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artistColumn = it.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val albumColumn = it.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val albumIdColumn = it.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
            val dataColumn = it.getColumnIndex(MediaStore.Audio.Media.DATA)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val title = it.getString(titleColumn)
                val artist = it.getString(artistColumn)
                val album = it.getString(albumColumn)
                val albumId = it.getLong(albumIdColumn)
                val data = it.getString(dataColumn)
                val track = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK))

                songList.add(Song(id, title, artist, album, albumId, data, null, track)) // `data` はファイルパス
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

    fun getArtistList(context: Context): List<Artist> {
        return getMusicList(context)
            .map { it.artist }
            .filter { it.isNotBlank() && it != "Unknown" }
            .distinct()
            .map { Artist(name = it) }
    }

    fun getSongsByAlbum(context: Context, albumId: Long): List<Song> {
        return getMusicList(context).filter { it.albumId == albumId }
    }

    fun getEmbeddedAlbumArt(context: Context, filePath: String): String? {
        val cacheFile = File(context.cacheDir, "album_art_${filePath.hashCode()}.webp")
        if (cacheFile.exists()) {
            Log.d("DEBUG", "Cached album art found: ${cacheFile.absolutePath}")
            return cacheFile.absolutePath
        }

        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(filePath)
            val art = retriever.embeddedPicture
            retriever.release()

            if (art != null) {
                FileOutputStream(cacheFile).use { fos ->
                    fos.write(art)
                }
                Log.d("DEBUG2", "🎨 Saved album art to cache: ${cacheFile.absolutePath}")
                cacheFile.absolutePath
            } else {
                Log.w("DEBUG2", "⚠️ No embedded album art found: $filePath")
                null
            }
        } catch (e: Exception) {
            Log.e("DEBUG2", "❌ Failed to get album art from $filePath: ${e.message}")
            null
        }
    }
}