package com.codef.digitalfan

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.core.view.GestureDetectorCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import java.util.Calendar

class MainActivity : ComponentActivity(),
    GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener {

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var alarmManager: AlarmManager
    private lateinit var alarmIntent: PendingIntent

    @OptIn(UnstableApi::class) override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinner: Spinner = findViewById(R.id.spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.alarm_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                val sharedPref = getSharedPreferences("DigitalFanPrefs", Context.MODE_PRIVATE)
                with (sharedPref.edit()) {
                    putString("selectedItem", selectedItem)
                    apply()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // This code will be executed when the Spinner selection is cleared
            }
        }

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            add(Calendar.MINUTE, 1)
            set(Calendar.SECOND, 0)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_FIFTEEN_MINUTES / 15,
            alarmIntent
        )

        gestureDetector = GestureDetectorCompat(this, this)
        gestureDetector.setOnDoubleTapListener(this)

        exoPlayer = ExoPlayerSingleton.getInstance(this)
        exoPlayer.setMediaItem(MediaItem.Builder()
            .setUri(Uri.parse("asset:///fan.mp3"))
            .setMimeType(MimeTypes.AUDIO_MPEG)
            .setClippingConfiguration(
                MediaItem.ClippingConfiguration.Builder()
                    .setStartPositionMs(1 * 60000)
                    .setEndPositionMs(28 * 60000)
                    .build()
            )
            .build())
        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
        exoPlayer.setHandleAudioBecomingNoisy(true)
        exoPlayer.prepare()

    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (gestureDetector.onTouchEvent(event)) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    override fun onDown(event: MotionEvent): Boolean {
        return true
    }

    override fun onLongPress(event: MotionEvent) {
    }

    override fun onFling(
        event1: MotionEvent?,
        event2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return true
    }

    override fun onShowPress(event: MotionEvent) {
    }

    override fun onSingleTapUp(event: MotionEvent): Boolean {
        return true
    }

    override fun onScroll(
        event1: MotionEvent?,
        event2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return true
    }

    override fun onDoubleTap(event: MotionEvent): Boolean {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
        return true
    }

    override fun onDoubleTapEvent(event: MotionEvent): Boolean {
        return true
    }

    override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
        return true
    }

}


