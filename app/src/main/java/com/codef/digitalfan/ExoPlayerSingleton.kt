package com.codef.digitalfan

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

object ExoPlayerSingleton {

    private var exoPlayer: ExoPlayer? = null

    fun getInstance(context: Context): ExoPlayer {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build()
        }
        return exoPlayer!!
    }

    fun setupExoPlayerFan(exoPlayerInstance: ExoPlayer) {

        exoPlayerInstance.setMediaItem(
            MediaItem.Builder()
                .setUri(Uri.parse("asset:///fan.mp3"))
                .setMimeType(MimeTypes.AUDIO_MPEG)
                .setClippingConfiguration(
                    MediaItem.ClippingConfiguration.Builder()
                        .setStartPositionMs(1 * 60000)
                        .setEndPositionMs(28 * 60000)
                        .build()
                )
                .build()
        )
        exoPlayerInstance.repeatMode = Player.REPEAT_MODE_ONE
        exoPlayerInstance.setHandleAudioBecomingNoisy(true)
        exoPlayerInstance.prepare()

    }

    fun setupExoPlayerAlarm(exoPlayerInstance: ExoPlayer) {

        exoPlayerInstance.setMediaItem(
            MediaItem.Builder()
                .setUri(Uri.parse("asset:///alarm.mp3"))
                .setMimeType(MimeTypes.AUDIO_MPEG)
                .build()
        )
        exoPlayerInstance.repeatMode = Player.REPEAT_MODE_ONE
        exoPlayerInstance.prepare()

    }

}