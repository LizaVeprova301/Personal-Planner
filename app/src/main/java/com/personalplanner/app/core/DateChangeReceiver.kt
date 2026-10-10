package com.personalplanner.app.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.personalplanner.app.widget.HabitWidget
import com.personalplanner.app.widget.MultiHabitWidget

class DateChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_DATE_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_TIME_CHANGED -> {
                HabitWidget.updateAll(context)
                MultiHabitWidget.updateAll(context)
            }
        }
    }
}