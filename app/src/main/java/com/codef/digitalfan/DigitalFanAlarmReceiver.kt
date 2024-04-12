package com.codef.digitalfan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class DigitalFanAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val exoPlayer = DigitalFanExoPlayerSingleton.getInstance(context)
        Log.d("bobo", "KICKING OFF ALARM!")
        DigitalFanExoPlayerSingleton.setupExoPlayerAlarm(exoPlayer)

    }

}