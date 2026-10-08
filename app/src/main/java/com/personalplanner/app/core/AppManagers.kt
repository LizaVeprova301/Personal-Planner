package com.personalplanner.app.core

import android.content.Context
import com.personalplanner.app.manager.HabitManager
import com.personalplanner.app.manager.TaskManager

class AppManagers(context: Context) {

    val taskManager =
        TaskManager.getInstance(context)

    val habitManager =
        HabitManager.getInstance(context)
}