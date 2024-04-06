package com.codef.digitalfan

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.core.view.GestureDetectorCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import java.util.Calendar

class MainActivity : ComponentActivity(),
    GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener {

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var alarmManager: AlarmManager
    private lateinit var alarmIntent: PendingIntent

    private var alarmKeys: MutableList<String> = mutableListOf()

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        exoPlayer = ExoPlayerSingleton.getInstance(this)
        ExoPlayerSingleton.setupExoPlayerFan(exoPlayer)

        loadAlarms()
        setUpAlarmManagerAndGestureDetector()
        setUpSpinner()

    }

    private fun setUpSpinner() {
        val spinner: Spinner = findViewById(R.id.spinner)
        val displayKeys = alarmKeys.map { it.substringBefore("|") }.toMutableList() // Create a new mutable list with the part before the pipe symbol
        displayKeys.sort()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, displayKeys)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                val selectedAlarmKey = alarmKeys.find { it.startsWith(selectedItem) }
                val selectedAlarmValue = selectedAlarmKey?.split("|")?.get(1)
                val selectedAlarmTimeArray = selectedAlarmValue?.split(" ")
                val selectedAlarmTime = selectedAlarmTimeArray?.get(0)
                val selectedAlarmAmPm = selectedAlarmTimeArray?.get(1)
                val sharedPref = getSharedPreferences("DigitalFanPrefs", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("wakeUpTime", selectedAlarmTime)
                    if (selectedAlarmTime != "No") {
                        Toast.makeText(
                            this@MainActivity,
                            "Alarm will sound at $selectedAlarmTime $selectedAlarmAmPm",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Alarm is turned off",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    apply()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // This code will be executed when the Spinner selection is cleared
            }
        }
    }

    private fun setUpAlarmManagerAndGestureDetector() {

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
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

    private fun buildDefaultAlarms() {
        addAlarm("No Alarm|No Alarm")
        addAlarm("Steve - In Office|07:50 AM")
        addAlarm("Steve - Work From Home|08:40 AM")
        addAlarm("Steve - Work From Home (On Call)|07:30 AM")
        addAlarm("Tenzin - In Office|07:30 AM")
        addAlarm("Tenzin - Work From Home|08:20 AM")
    }

    private fun addAlarm(key: String) {
        alarmKeys.add(key)
        saveAlarms()
    }

    private fun saveAlarms() {
        val sharedPref = getSharedPreferences("DigitalFanPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putStringSet("alarmKeys", alarmKeys.toSet())
            apply()
        }
    }

    private fun loadAlarms() {
        val sharedPref = getSharedPreferences("DigitalFanPrefs", Context.MODE_PRIVATE)
        alarmKeys =
            sharedPref.getStringSet("alarmKeys", emptySet())?.toMutableList() ?: mutableListOf()

        // If alarmKeys and alarmValues are empty, initialize them with default values
        if (alarmKeys.isEmpty()) {
            buildDefaultAlarms()
        }
    }

//    fun removeAlarm(index: Int) {
//        if (index in alarmKeys.indices) {
//            alarmKeys.removeAt(index)
//            saveAlarms()
//        }
//    }
//
//    fun modifyAlarm(index: Int, newKey: String) {
//        if (index in alarmKeys.indices) {
//            alarmKeys[index] = newKey
//            saveAlarms()
//        }
//    }
//
//

//    val manageButton: Button = findViewById(R.id.manage_button)
//    manageButton.setOnClickListener {
//        val intent = Intent(this, ManageAlarmsActivity::class.java)
//        intent.putStringArrayListExtra("alarmKeys", ArrayList(alarmKeys))
//        startActivity(intent)
//    }

}


