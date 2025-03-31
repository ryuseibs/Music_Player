package com.example.music_player

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!PermissionManager.checkPermission(this)) {
            PermissionManager.onRequestPermissionsResult(this)
        } else {
            initViewModel()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionManager.isPermissionGranted(requestCode, grantResults)) {
            initViewModel()
        } else {
            Toast.makeText(this, "権限が必要です", Toast.LENGTH_SHORT).show()
        }
        Log.d("Permission","requestCode:$requestCode,perimissions:$permissions,grantResults:$grantResults")
    }

    private fun initViewModel() {
        val factory = MusicViewModelFactory(application)
        val musicViewModel = ViewModelProvider(this, factory).get(MusicViewModel::class.java)

        lifecycleScope.launch {
            val songs = musicViewModel.songList.first { it.isNotEmpty() }
            Log.d("DEBUG", "songList size: ${songs.size}")

            val songId = intent.getLongExtra("songId", -1)
            val albumId = intent.getLongExtra("albumId",-1)
            Log.d("DEBUG", "Received songId: $songId")
            Log.d("DEBUG", "Received albumId: $albumId")

            if (songId != -1L && albumId != -1L) {
                val albumSongs = MusicRepository.getSongsByAlbum(applicationContext, albumId)
                musicViewModel.setAlbumSongList(applicationContext, albumSongs, songId)
            }

            setContent {
                PlayerScreen(viewModel = musicViewModel)
            }
        }
    }
}