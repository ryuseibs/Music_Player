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

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, "music_channel_id")
            .setContentTitle("音楽再生中")
            .setContentText("アプリ閉じても再生続けます")
            .setSmallIcon(R.drawable.ic_music_note)
            .build()
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "music_channel_id",
                "音楽再生チャンネル",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }
}