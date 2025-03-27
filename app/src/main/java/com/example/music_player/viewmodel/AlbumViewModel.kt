package com.example.music_player.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music_player.MusicRepository
import com.example.music_player.model.Album
import com.example.music_player.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlbumViewModel: ViewModel() {
    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums

    fun loadAlbumsByArtist(context: Context, artistName: String) {
        viewModelScope.launch {
            val allSongs = MusicRepository.getMusicList(context)

            val filterAlbums = allSongs
                .filter { it.artist == artistName }
                .groupBy { it.albumId }
                .map { (albumId, songGroup) ->
                    Album(
                        albumId = albumId,
                        albumName = songGroup.first().album,
                        artist = songGroup.first().artist,
                        albumArtPath = songGroup.first().albumArtPath
                    )
                }
            _albums.value = filterAlbums
        }
    }
}