package com.personalplanner.app.core

import android.content.Context
import com.personalplanner.app.widget.TaskWidget
import com.personalplanner.app.widget.HabitWidget

object AppInitializer {

    fun initialize(context: Context) {
        TaskWidget.updateAll(context)
        HabitWidget.updateAll(context)
    }
}