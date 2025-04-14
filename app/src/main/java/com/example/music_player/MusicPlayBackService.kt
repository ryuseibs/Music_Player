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
        when (intent?.action) {
            "PLAY" -> {
                val path = intent.getStringExtra("songPath")
                path?.let {
                    startForeground(1, buildNotification())
                    playSong(it) // ← ここでサービス側の playSong() を呼び出す
                }
            }
        }
        return START_STICKY
    }

    private fun playSong(path: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(path)
            prepare()
            start()
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf() // サービスを停止して、再生も終了させる
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