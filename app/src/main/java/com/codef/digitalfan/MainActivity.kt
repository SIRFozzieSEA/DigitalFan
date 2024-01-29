package com.codef.digitalfan

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer

class MainActivity : ComponentActivity() {

    private lateinit var exoPlayer: ExoPlayer

    @OptIn(UnstableApi::class) override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mediaItemClipped =
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

        exoPlayer = ExoPlayer.Builder(this).build()
        exoPlayer.setMediaItem(mediaItemClipped)
        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
        exoPlayer.setHandleAudioBecomingNoisy(true)
        exoPlayer.prepare()
        exoPlayer.play()

    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }

}


