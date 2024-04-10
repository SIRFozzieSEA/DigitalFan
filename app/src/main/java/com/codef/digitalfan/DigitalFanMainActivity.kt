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
    private lateinit var alarmManager: AlarmManager
    private lateinit var alarmIntent: PendingIntent

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

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
            val hourValueOriginal = spinner1.selectedItem.toString()
            var hourValue = spinner1.selectedItem.toString()
            val minutesValue = spinner2.selectedItem.toString()
            val amPmValue = spinner3.selectedItem.toString()

            if (amPmValue == "PM") {
                hourValue = (hourValue.toInt() + 12).toString()
            }

            Log.d("bobo", "Setting alarm for $hourValue:$minutesValue $amPmValue")

            alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            val intent = Intent(this, DigitalFanAlarmReceiver::class.java)
            alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val calendar: Calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hourValue.toInt())
                set(Calendar.MINUTE, minutesValue.toInt())
            }

            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmIntent)
            Toast.makeText(this, "Alarm is set for $hourValueOriginal:$minutesValue $amPmValue", Toast.LENGTH_LONG).show()

        }

        val turnOffAlarmButton: Button = findViewById(R.id.turnOffAlarmButton)
        turnOffAlarmButton.setOnClickListener {
            stopAlarmAndResetExoplayer()
        }

        Toast.makeText(this, "Alarm is not currently set.", Toast.LENGTH_LONG).show()

    }



    private fun stopAlarmAndResetExoplayer() {
        if (::alarmManager.isInitialized && ::alarmIntent.isInitialized) {
            alarmManager.cancel(alarmIntent)
            Log.d("bobo", "Alarm turned off.")
        } else {
            Log.d("bobo", "AlarmManager and alarmIntent not initialized.")
        }
        DigitalFanExoPlayerSingleton.setupExoPlayerFan(exoPlayer)
    }

    override fun onStop() {
        super.onStop()
        stopAlarmAndResetExoplayer()
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


