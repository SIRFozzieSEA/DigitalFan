package com.codef.digitalfan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val exoPlayer = ExoPlayerSingleton.getInstance(context)
        val isAlarming = Utils.getAppPreferenceString(context, "isAlarming", "false")
        if (!isAlarming.toBoolean()) {
            Log.d("bobo", "KICKING OFF ALARM!")
            ExoPlayerSingleton.setupExoPlayerAlarm(exoPlayer)
            Utils.setAppPreferenceString(context, "isAlarming","true")
        } else {
            Log.d("bobo", "Alarm already in progress.")
        }
    }

}