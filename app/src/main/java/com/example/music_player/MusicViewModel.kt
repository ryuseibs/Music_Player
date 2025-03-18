package com.example.music_player

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MusicViewModel : ViewModel() {
    private val _currentSong = MutableStateFlow(
        Song("sample song","sample artist",R.drawable.placeholder_artwork)
    )
    val currentSong: StateFlow<Song> = _currentSong
}