package com.personalplanner.app.core

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.personalplanner.app.manager.TaskManager

class AppLifecycleObserver(
    private val taskManager: TaskManager
) : DefaultLifecycleObserver {

    override fun onResume(owner: LifecycleOwner) {
        taskManager.reloadTasks()
    }
}