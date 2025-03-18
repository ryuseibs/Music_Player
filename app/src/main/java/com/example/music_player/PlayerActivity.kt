package com.example.music_player

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!PermissionManager.checkPermission(this)) {
            PermissionManager.onRequestPermissionsResult(this)
        }

        val MusicViewModel: MusicViewModel = ViewModelProvider(this).get(MusicViewModel::class.java)

        setContent {
            PlayerScreen(MusicViewModel)
        }
    }
}