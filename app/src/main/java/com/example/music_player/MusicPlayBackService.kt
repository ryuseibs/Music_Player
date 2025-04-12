package com.example.music_player

import android.media.MediaPlayer
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class MusicPlayBackService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val songPath = intent?.getStringExtra("songPath")

        if (songPath != null) {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(songPath)
                prepare()
                start()
            }
        }

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun createNotificationChannel() {
        TODO("Not yet implemented")
    }
}