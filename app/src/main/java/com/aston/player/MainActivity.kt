package com.aston.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var songName: TextView
    private var currentSongIndex = 0
    private var musicService: MusicService? = null
    private var isServiceBound = false

    private val audioFiles = arrayOf("first.mp3", "second.mp3", "third.mp3")


    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.LocalBinder
            musicService = binder.getService()
            isServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var assetFileDescriptor: AssetFileDescriptor = assets.openFd("first.mp3")

        val playIcon: ImageView = findViewById(R.id.playSong)
        val stopIcon: ImageView = findViewById(R.id.stopSong)
        val nextIcon: ImageView = findViewById(R.id.nextSong)
        val previousIcon: ImageView = findViewById(R.id.previousSong)
        songName = findViewById(R.id.songName)
        songName.text = audioFiles[currentSongIndex]
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
            mediaPlayer?.prepare()
        }

        updateIconsVisibility()

        playIcon.setOnClickListener {
            if (isServiceBound) {
                if (musicService?.isPlaying() == true) {
                    musicService?.pauseMusic()
                } else {
                    val currentSong = audioFiles[currentSongIndex]
                    if (musicService?.isPaused() == true) {
                        musicService?.resumeMusic()
                    } else {
                        musicService?.playMusic(currentSong)
                    }
                }
                updateIconsVisibility()
            }
        }


        stopIcon.setOnClickListener {
            if (isServiceBound) {
                musicService?.pauseMusic()
                updateIconsVisibility()
            }
        }

        nextIcon.setOnClickListener {
            if (isServiceBound) {
                currentSongIndex = (currentSongIndex + 1) % audioFiles.size
                val nextSong = audioFiles[currentSongIndex]
                musicService?.playMusic(nextSong)
                updateSongName(nextSong)
                updateIconsVisibility()
            }
        }

        previousIcon.setOnClickListener {
            if (isServiceBound) {
                currentSongIndex = (currentSongIndex - 1 + audioFiles.size) % audioFiles.size
                val previousSong = audioFiles[currentSongIndex]
                musicService?.playMusic(previousSong)
                updateSongName(previousSong)
                updateIconsVisibility()
            }
        }
        val serviceIntent = Intent(this, MusicService::class.java)
        startService(serviceIntent)
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        if (isServiceBound) {
            unbindService(connection)
            isServiceBound = false
        }
        super.onDestroy()
    }



    private fun updateIconsVisibility() {
        val playIcon: ImageView = findViewById(R.id.playSong)
        val stopIcon: ImageView = findViewById(R.id.stopSong)
        val isPlaying = musicService?.isPlaying() ?: false
        if (isPlaying) {
            stopIcon.visibility = View.VISIBLE
            playIcon.visibility = View.INVISIBLE
        } else {
            stopIcon.visibility = View.INVISIBLE
            playIcon.visibility = View.VISIBLE
        }
    }

    private fun updateSongName(song: String) {
        songName.text = song
    }



}