package com.personalplanner.app.core

import android.content.Context
import com.personalplanner.app.widget.TaskWidget

object AppInitializer {

    fun initialize(context: Context) {
        TaskWidget.updateAll(context)
    }
}