package com.example.music_player.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music_player.MusicRepository
import com.example.music_player.model.Artist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ArtistViewModel() : ViewModel() {
    private val repository = MusicRepository

    private  val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists

    fun loadArtists(context: Context) {
        viewModelScope.launch {
            _artists.value = repository.getArtistList(context)
        }
    }
}