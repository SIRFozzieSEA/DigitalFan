package com.codef.digitalfan

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.core.view.GestureDetectorCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import java.util.Calendar

class DigitalFanMainActivity : ComponentActivity(),
    GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener {

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var alarmIntent: PendingIntent
    private lateinit var alarmManager: AlarmManager

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        exoPlayer = DigitalFanExoPlayerSingleton.getInstance(this)
        DigitalFanExoPlayerSingleton.setupExoPlayerFan(exoPlayer)

        setUpSpinnerAndButtons()

        gestureDetector = GestureDetectorCompat(this, this)
        gestureDetector.setOnDoubleTapListener(this)

    }

    private fun setUpSpinnerAndButtons() {

        val spinner1: Spinner = findViewById(R.id.spinnerHour)
        val numbers = (1..12).map { String.format("%02d", it) } // Create a list of strings from 1 to 12
        val adapter = ArrayAdapter(this, R.layout.spinner_item, numbers)
        adapter.setDropDownViewResource(R.layout.spinner_item)
        spinner1.adapter = adapter

        val defaultHour = DigitalFanUtils.getAppPreferenceString(this, "spinnerHour", "07")
        val defaultHourPosition = numbers.indexOf(defaultHour)
        if (defaultHourPosition != -1) {
            spinner1.setSelection(defaultHourPosition)
        }

        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedValue = parent.getItemAtPosition(position).toString()
                DigitalFanUtils.setAppPreferenceString(this@DigitalFanMainActivity, "spinnerHour", selectedValue)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        val spinner2: Spinner = findViewById(R.id.spinnerMinutes)
        val minutes = (0..55 step 5).map { String.format("%02d", it) } // Create a list of strings from "00" to "55" in increments of 5
        val adapter2 = ArrayAdapter(this, R.layout.spinner_item, minutes)
        adapter2.setDropDownViewResource(R.layout.spinner_item)
        spinner2.adapter = adapter2

        val defaultMinute = DigitalFanUtils.getAppPreferenceString(this, "spinnerMinutes", "50")
        val defaultMinutePosition = minutes.indexOf(defaultMinute)
        if (defaultMinutePosition != -1) {
            spinner2.setSelection(defaultMinutePosition)
        }

        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedValue = parent.getItemAtPosition(position).toString()
                DigitalFanUtils.setAppPreferenceString(this@DigitalFanMainActivity, "spinnerMinutes", selectedValue)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        val spinner3: Spinner = findViewById(R.id.spinnerAmPm)
        val amPmValues = arrayOf("AM", "PM") // Create an array with "AM" and "PM"
        val adapter3 = ArrayAdapter(this, R.layout.spinner_item, amPmValues)
        adapter3.setDropDownViewResource(R.layout.spinner_item)
        spinner3.adapter = adapter3

        val defaultAmPm = DigitalFanUtils.getAppPreferenceString(this, "spinnerAmPm", "AM")
        val defaultAmPmPosition = amPmValues.indexOf(defaultAmPm)
        if (defaultAmPmPosition != -1) {
            spinner3.setSelection(defaultAmPmPosition)
        }

        spinner3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedValue = parent.getItemAtPosition(position).toString()
                DigitalFanUtils.setAppPreferenceString(this@DigitalFanMainActivity, "spinnerAmPm", selectedValue)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        val setAlarmButton: Button = findViewById(R.id.setAlarmButton)
        setAlarmButton.setOnClickListener {

            val hourValueOriginal = spinner1.selectedItem.toString().toInt()
            var hourValue = spinner1.selectedItem.toString().toInt()
            val minutesValue = spinner2.selectedItem.toString().toInt()
            val amPmValue = spinner3.selectedItem.toString()

            if (amPmValue == "PM" && hourValue != 12) {
                hourValue += 12
            } else if (amPmValue == "AM" && hourValue == 12) {
                hourValue = 1
            }

            val currentTime = Calendar.getInstance()
            Log.d("bobo", "Current time: " + currentTime.time.toString())
            Log.d("bobo", "Current time: " + currentTime.timeInMillis)

            var dayOffset = 0
            var alarmTime: Calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hourValue)
                set(Calendar.MINUTE, minutesValue)
                set(Calendar.SECOND, 0)
                add(Calendar.DAY_OF_MONTH, dayOffset)
            }

            if (alarmTime.timeInMillis < currentTime.timeInMillis) {
                dayOffset = 1
                alarmTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hourValue)
                    set(Calendar.MINUTE, minutesValue)
                    set(Calendar.SECOND, 0)
                    add(Calendar.DAY_OF_MONTH, dayOffset)
                }
            }

            Log.d("bobo", "  Alarm time: " + alarmTime.time.toString())
            Log.d("bobo", "  Alarm time: " + alarmTime.timeInMillis)

            if (::alarmManager.isInitialized && ::alarmIntent.isInitialized) {
                alarmManager.cancel(alarmIntent)
            }

            val intent = Intent(this, DigitalFanAlarmReceiver::class.java)
            alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE)
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime.timeInMillis, alarmIntent)

            Toast.makeText(this, "Alarm set for " + String.format("%02d", hourValueOriginal)
                    + ":" + String.format("%02d", minutesValue) + " $amPmValue " + if (dayOffset == 0) "today" else "tomorrow", Toast.LENGTH_SHORT).show()


        }

        val turnOffAlarmButton: Button = findViewById(R.id.turnOffAlarmButton)
        turnOffAlarmButton.setOnClickListener {
            stopAlarmAndResetReleaseExoplayer(false)
        }

    }

    private fun stopAlarmAndResetReleaseExoplayer(releaseExoplayer: Boolean) {
        if (::alarmManager.isInitialized && ::alarmIntent.isInitialized) {
            alarmManager.cancel(alarmIntent)
            Toast.makeText(this, "Alarm cancelled.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "AlarmManager and alarmIntent not initialized.", Toast.LENGTH_SHORT).show()
        }
        if (releaseExoplayer) {
            exoPlayer.release()
        } else {
            DigitalFanExoPlayerSingleton.setupExoPlayerFan(exoPlayer)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAlarmAndResetReleaseExoplayer(true)
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


