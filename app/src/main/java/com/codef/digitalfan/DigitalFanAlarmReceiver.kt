package com.codef.digitalfan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DigitalFanAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val exoPlayer = DigitalFanExoPlayerSingleton.getInstance(context)
        DigitalFanExoPlayerSingleton.setupExoPlayerAlarm(exoPlayer)

    }

}