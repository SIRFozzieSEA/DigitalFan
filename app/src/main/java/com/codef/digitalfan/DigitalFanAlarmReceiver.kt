package com.codef.digitalfan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class DigitalFanAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        for (i in 0..100) {
            Log.d("bobo", "----------- ALARMING!!! -----------")
        }

        val exoPlayer = DigitalFanExoPlayerSingleton.getInstance(context)
        DigitalFanExoPlayerSingleton.setupExoPlayerAlarm(exoPlayer)

    }

}