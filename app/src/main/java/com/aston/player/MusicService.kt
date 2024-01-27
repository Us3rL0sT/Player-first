package com.aston.player

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder

class MusicService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }


    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }

    fun playMusic(audioFile: String) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        } else {
            mediaPlayer?.reset()
        }
        val assetFileDescriptor = assets.openFd(audioFile)
        mediaPlayer?.setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
        mediaPlayer?.prepare()
        mediaPlayer?.start()

        isPlaying = true
    }

    fun pauseMusic() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
        isPlaying = false
    }

    fun resumeMusic() {
        if (!mediaPlayer?.isPlaying!!) {
            mediaPlayer?.start()
            isPlaying = true
        }
    }

    fun isPaused(): Boolean {
        return mediaPlayer?.isPlaying == false && (mediaPlayer?.currentPosition ?: 0) > 0
    }

    fun isPlaying(): Boolean {
        return isPlaying
    }
}


