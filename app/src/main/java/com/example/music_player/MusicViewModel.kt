package com.example.music_player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MusicViewModel : ViewModel() {
    private val songList = listOf(
        Song("Sample Song 1", "Artist 1", R.drawable.placeholder_artwork),
        Song("Sample Song 2", "Artist 2", R.drawable.placeholder_artwork),
        Song("Sample Song 3", "Artist 3", R.drawable.placeholder_artwork)
    )
    private val _currentSongIndex = MutableStateFlow(0)
    val currentSong: StateFlow<Song> = _currentSongIndex.map { songList[it] }
        .stateIn(viewModelScope, SharingStarted.Lazily, songList[0])

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    fun play() {
        _isPlaying.value = true
    }

    fun pause() {
        _isPlaying.value = false
    }

    fun nextTrack() {
        if (_currentSongIndex.value < songList.size - 1) {
            _currentSongIndex.value +=1
        }
    }

    fun previousTrack() {
        if (_currentSongIndex.value > 0) {
            _currentSongIndex.value -= 1
        }
    }
}