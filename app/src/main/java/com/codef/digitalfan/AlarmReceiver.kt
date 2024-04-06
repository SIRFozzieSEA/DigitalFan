package com.codef.digitalfan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.time.LocalTime

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val exoPlayer = ExoPlayerSingleton.getInstance(context)

        val sharedPref = context.getSharedPreferences("DigitalFanPrefs", Context.MODE_PRIVATE)
        val wakeUpTime = sharedPref.getString("wakeUpTime", "No")
        val currentIsAlarming = sharedPref.getBoolean("isAlarming", false)

        val currentTime = LocalTime.now()
        val currentHour = currentTime.hour
        val currentMinute = currentTime.minute
        val currentTimeParsed = LocalTime.of(currentHour, currentMinute)

        Log.d("bobo", "AlarmReceiver kicked off, current time = $currentTimeParsed")

        if (wakeUpTime != "No") {

            val wakeUpTimeParsed = LocalTime.parse(wakeUpTime)
            Log.d("bobo", "AlarmReceiver kicked off, alarm time = $wakeUpTimeParsed")

            if (!currentIsAlarming) {
                if (currentTimeParsed.equals(wakeUpTimeParsed) || currentTimeParsed.isAfter(
                        wakeUpTimeParsed
                    )
                ) {
                    Log.d("bobo", "KICKING OFF ALARM!")
                    ExoPlayerSingleton.setupExoPlayerAlarm(exoPlayer)
                }
            } else {
                Log.d("bobo", "Alarm is running currently, not kicking off again.")
            }

            setIsAlarming(context, true, currentIsAlarming)

        } else {
            if (currentIsAlarming) {
                ExoPlayerSingleton.setupExoPlayerFan(exoPlayer)
            }
            setIsAlarming(context, false, currentIsAlarming)
        }

        Log.d("bobo", "--------------------------------------------------")

    }

    private fun setIsAlarming(context: Context, isAlarming: Boolean, currentIsAlarming: Boolean) {
        if (isAlarming != currentIsAlarming) {
            val sharedPref = context.getSharedPreferences("DigitalFanPrefs", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putBoolean("isAlarming", isAlarming)
                apply()
            }
        }
    }

}