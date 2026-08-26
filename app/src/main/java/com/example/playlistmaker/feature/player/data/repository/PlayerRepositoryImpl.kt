package com.example.playlistmaker.feature.player.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.feature.player.domain.repository.PlayerRepository

class PlayerRepositoryImpl : PlayerRepository {

    private var mediaPlayer: MediaPlayer? = null

    override fun preparePlayer(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    ) {
        release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                onPrepared()
            }
            setOnCompletionListener {
                onCompletion()
            }
        }
    }

    override fun start() {
        mediaPlayer?.start()
    }

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }
}