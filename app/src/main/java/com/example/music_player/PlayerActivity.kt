package com.example.music_player

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

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
        val factory = MusicViewModelFactory(this)
        val musicViewModel = ViewModelProvider(this, factory).get(MusicViewModel::class.java)

        setContent {
            PlayerScreen(musicViewModel)
        }
    }
}