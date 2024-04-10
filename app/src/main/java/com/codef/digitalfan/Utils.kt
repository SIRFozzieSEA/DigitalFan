package com.codef.digitalfan

import android.content.Context
import android.util.Log

class Utils {

    companion object {

        fun setAppPreferenceString(context: Context, key: String, newValue: String) {
            val sharedPref = context.getSharedPreferences("DigitalFanPrefs", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                // Log.d("bobo", "$key is being set to $newValue.")
                putString(key, newValue)
                apply()
            }
        }

        fun getAppPreferenceString(context: Context, key: String, defaultValue: String): String? {
            val sharedPref = context.getSharedPreferences("DigitalFanPrefs", Context.MODE_PRIVATE)
            return sharedPref.getString(key, defaultValue)
        }

    }

}