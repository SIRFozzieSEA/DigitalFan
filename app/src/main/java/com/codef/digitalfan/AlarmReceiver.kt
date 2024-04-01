package com.codef.digitalfan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import java.time.LocalTime

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val exoPlayer = ExoPlayerSingleton.getInstance(context)
        val sharedPref = context.getSharedPreferences("DigitalFanPrefs", Context.MODE_PRIVATE)
        val selectedItem = sharedPref.getString("selectedItem", "No Alarm")

        if (selectedItem != "No Alarm") {
            val currentTime = LocalTime.now()
            val currentHour = currentTime.hour
            val currentMinute = currentTime.minute
            val givenTime = if (selectedItem == "Work From Home") {
                LocalTime.parse("16:50")
            } else {
                LocalTime.parse("07:49")
            }

            val currentTimeNoSeconds = LocalTime.of(currentHour, currentMinute)
            if (currentTimeNoSeconds.equals(givenTime) && currentTime.isAfter(givenTime)) {
                exoPlayer.setMediaItem(
                    MediaItem.Builder()
                        .setUri(Uri.parse("asset:///alarm.mp3"))
                        .setMimeType(MimeTypes.AUDIO_MPEG)
                        .build())
                exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
                exoPlayer.prepare()
            }
        }

    }
}