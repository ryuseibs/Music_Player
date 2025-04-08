package com.example.music_player

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ReportFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.example.music_player.model.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

enum class RepeatMode {
    off, all, one
}

class MusicViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val _songList = MutableStateFlow<List<Song>>(emptyList())
    val songList: StateFlow<List<Song>> = _songList

    private val _currentSongIndex = MutableStateFlow(0)
    val currentSong: StateFlow<Song> = _currentSongIndex.map {
        _songList.value.getOrNull(it) ?: Song(
            0L,
            "",
            "",
            "",
            0L,
            "",
            "",
            0,
            0
        )
    }
        .stateIn(viewModelScope, SharingStarted.Lazily, Song(0L, "", "", "", 0L, "", "",0,0))

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private var mediaPlayer: MediaPlayer? = null

    private val _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> = _currentPosition

    private val _duration = MutableStateFlow(0)
    val duration: StateFlow<Int> = _duration

    private val _remainingTime = MutableStateFlow(0)
    val remainingTime: StateFlow<Int> = _remainingTime

    private val _dominantColor = mutableStateOf(Color.Black)
    val dominantColor: State<Color> = _dominantColor

    private val _volume = mutableStateOf(5)  // 初期値は適宜
    val volume: State<Int> = _volume

    private val _isShuffleEnabled = mutableStateOf(false)
    val isShuffleEnabled: State<Boolean> = _isShuffleEnabled

    private val _repeatMode = mutableStateOf(RepeatMode.off)
    val repeatMode: State<RepeatMode> = _repeatMode

    private val _songsByAlbum = MutableStateFlow<List<Song>>(emptyList())
    val songsByAlbum: StateFlow<List<Song>> = _songsByAlbum

    val recentAlbums: StateFlow<List<Album>> = songList.map { songs ->
        songs
            .groupBy { it.albumId }
            .map { (albumId, group) ->
                val first = group.first()
                Album(
                    albumId = albumId,
                    albumName = first.album,
                    artist = first.artist,
                    albumArtPath = group.firstOrNull { it.albumArtPath != null }?.albumArtPath,
                    albumYear = first.year,
                    albumSongCount = group.size
                )
            }
            .sortedByDescending { it.albumYear } // or 他のロジックで最近追加順にする
            .take(10)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        loadMusicList(context)
        viewModelScope.launch {
            while (_isPlaying.value) {
                _songList.value = MusicRepository.getMusicList(context)
                _currentPosition.value = mediaPlayer?.currentPosition ?: 0
                _duration.value = mediaPlayer?.duration ?: 0
                _remainingTime.value = maxOf((_duration.value) - (_currentPosition.value), 0)
                delay(500)
            }
        }
    }

    private fun loadMusicList(context: Context) {
        val songs = MusicRepository.getMusicList(context) // 端末の音楽を取得！
        if (songs.isNotEmpty()) {
            _songList.value = songs
        }
    }

    fun startProgressUpdater() {
        viewModelScope.launch {
            while (isActive) {
                mediaPlayer?.let {
                    _currentPosition.value = it.currentPosition
                    _duration.value = it.duration
                    _remainingTime.value = it.duration - it.currentPosition
                }
                delay(500) // 0.5秒ごとに更新
            }
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
                if (_repeatMode.value == RepeatMode.one) {
                    seekTo(0)
                    start()
                } else {
                    nextTrack(context)
                }
            }
        }
        _isPlaying.value = true
        startProgressUpdater()
    }

    fun playSong(path: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(path)
            prepare()
            start()
        }
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
        _currentPosition.value = position
    }

    fun pauseForSeek() {
        mediaPlayer?.pause()
    }

    fun resumeAfterSeek() {
        mediaPlayer?.start()
        startProgressUpdater()
    }

    fun play(context: Context) {
        if (mediaPlayer == null) {
            playCurrentTrack(context)
        } else {
            mediaPlayer?.start()
            _isPlaying.value = true
            startProgressUpdater()
        }
    }

    fun pause() {
        mediaPlayer?.pause()
        _isPlaying.value = false
    }

    fun nextTrack(context: Context) {
        val listSize = _songList.value.size
        if (_isShuffleEnabled.value && listSize > 1) {
            var randomIndex: Int
            do {
                randomIndex = (0 until listSize).random()
            } while (randomIndex == _currentSongIndex.value) // 同じ曲を回避
            _currentSongIndex.value = randomIndex
        } else {
            if (_currentSongIndex.value < listSize - 1) {
                _currentSongIndex.update { it + 1 }
            } else {
                _currentSongIndex.value = 0 // 末尾のときは先頭に戻る
            }
            _currentPosition.value = 0
            playCurrentTrack(context)
        }
    }

    fun previousTrack(context: Context) {
        val currentPos = mediaPlayer?.currentPosition ?: 0

        if (currentPos > 1000) {
            mediaPlayer?.seekTo(0)
            _currentPosition.value = 0
        } else {
            if (_currentSongIndex.value > 0) {
                _currentSongIndex.update { it - 1 }
            } else {
                _currentSongIndex.value = _songList.value.size - 1
            }
            _currentPosition.value = 0
            playCurrentTrack(context)
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
    }

    fun setVolume(volume: Float, context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val newVolume = (volume / 15f * maxVolume).toInt().coerceIn(0, maxVolume)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0)
        _volume.value = newVolume
    }

    fun getArtworkBitmapFromPath(path: String?): Bitmap? {
        return path?.let {
            BitmapFactory.decodeFile(it)
        }
    }

    fun updateArtworkColor(bitmap: Bitmap) {
        Palette.from(bitmap).generate { palette ->
            val colorInt = palette?.getDominantColor(android.graphics.Color.BLACK)
                ?: android.graphics.Color.BLACK
            _dominantColor.value = Color(colorInt)
        }
    }

    fun updateVolumeFromSystem(value: Int) {
        _volume.value = value
    }

    fun initializeVolume(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        _volume.value = currentVolume
    }

    fun toggleShuffle() {
        _isShuffleEnabled.value = !_isShuffleEnabled.value
    }

    fun toggleRepeat() {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.off -> RepeatMode.all
            RepeatMode.all -> RepeatMode.one
            RepeatMode.one -> RepeatMode.off
        }
    }

    fun loadSongsByAlbum(albumId: Long) {
        viewModelScope.launch {
//            val allSongs = MusicRepository.getMusicList(context)
            _songsByAlbum.value = _songList.value.filter { it.albumId == albumId }
        }
    }

    fun setCurrentSongById(songId: Long) {
        val index = _songList.value.indexOfFirst { it.id == songId }
        if (index != -1) {
            _currentSongIndex.value = index
            Log.d("DEBUG", "✔ currentSongIndex set to $index")
        }
        Log.w("MusicViewModel", "⚠ songId $songId not found in current songList")
    }

    fun setAlbumSongList(
        context: Context,
        albumSongs: List<Song>,
        selectedSongId: Long) {

        val sortedSongs = albumSongs.sortedBy { it.trackNumber }

        _songList.value = sortedSongs
        val index = sortedSongs.indexOfFirst { it.id == selectedSongId }
        if (index != -1) {
            _currentSongIndex.value = index
            Log.d("DEBUG", "✔ 現在のインデックス = $index, 選ばれたID = ${selectedSongId}")
        }
    }
}