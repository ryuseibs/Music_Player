package com.example.music_player

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlbumDetailViewModel(): ViewModel() {
    private val musicRepository = MusicRepository
    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs

    fun loadSongs(context: Context, albumId: Long) {
        viewModelScope.launch {
            val filterSongs = musicRepository.getSongsByAlbum(context, albumId)
            _songs.value = filterSongs

        }
    }
}