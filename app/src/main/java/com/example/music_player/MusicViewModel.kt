package com.example.music_player

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class MusicViewModel(private val context: Context) : ViewModel() {
    private val _songList = MutableStateFlow<List<Song>>(emptyList())
    val songList: StateFlow<List<Song>> = _songList

    private val _currentSongIndex = MutableStateFlow(0)
    val currentSong: StateFlow<Song> = _currentSongIndex.map { _songList.value.getOrNull(it) ?: Song("","","","","")}
        .stateIn(viewModelScope, SharingStarted.Lazily, Song("","","","",""))

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private var mediaPlayer: MediaPlayer? = null

    private val _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> = _currentPosition

    private val _duration = MutableStateFlow(0)
    val duration: StateFlow<Int> = _duration

    private val _remainingTime = MutableStateFlow(0)
    val remainingTime: StateFlow<Int> = _remainingTime

    init {
        loadMusicList(context)
    }

    private fun loadMusicList(context: Context) {
        val songs = MusicRepository.getMusicList(context) // 端末の音楽を取得！
        if (songs.isNotEmpty()) {
            _songList.value = songs
        }
    }

    fun playCurrentTrack(context: Context) {
        mediaPlayer?.release()
        val songPath = _songList.value.getOrNull(_currentSongIndex.value)?.filePath ?: return
        mediaPlayer = MediaPlayer().apply {
            setDataSource(songPath)
            prepare()
            start()
            _duration.value = duration
            setOnCompletionListener {
                nextTrack(context)
            }
        }

        _isPlaying.value = true

        viewModelScope.launch {
            while (_isPlaying.value) {
                _currentPosition.value = mediaPlayer?.currentPosition ?: 0
                _duration.value = mediaPlayer?.duration ?: 0
                _remainingTime.value = maxOf((_duration.value) - (_currentPosition.value),0)
                delay(500)
            }
        }
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    fun play() {
        mediaPlayer?.pause()
        _isPlaying.value = true
    }

    fun pause() {
        mediaPlayer?.pause()
        _isPlaying.value = false
    }

    fun nextTrack(context: Context) {
        if (_currentSongIndex.value < _songList.value.size - 1) {
            _currentSongIndex.update { it + 1 }
            if (_isPlaying.value) {
                playCurrentTrack(context)
            }
        }
    }

    fun previousTrack(context: Context) {
        if (_currentSongIndex.value > 0) {
            _currentSongIndex.update { it - 1 }
            if (_isPlaying.value) {
                playCurrentTrack(context)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
    }

}