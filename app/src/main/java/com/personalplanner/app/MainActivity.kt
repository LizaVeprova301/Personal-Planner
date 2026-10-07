package com.personalplanner.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.personalplanner.app.manager.TaskManager
import com.personalplanner.app.ui.ArchiveScreen
import com.personalplanner.app.ui.TaskScreen
import com.personalplanner.app.ui.theme.PersonalPlannerTheme
import com.personalplanner.app.widget.TaskWidget
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

enum class AppScreen {
    TASKS,
    ARCHIVE
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val taskManager = TaskManager.getInstance(this)

        lifecycle.addObserver(
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    taskManager.reloadTasks()
                }
            }
        )

        setContent {
            PersonalPlannerTheme {

                // update widget only after TaskManager is initialized
                remember {
                    TaskWidget.updateAll(this@MainActivity)
                    null
                }

                var currentScreen by remember {
                    mutableStateOf(AppScreen.TASKS)
                }

                when (currentScreen) {
                    AppScreen.TASKS -> {
                        TaskScreen(
                            taskManager = taskManager,
                            onOpenArchive = {
                                currentScreen = AppScreen.ARCHIVE
                            }
                        )
                    }

                    AppScreen.ARCHIVE -> {
                        ArchiveScreen(
                            taskManager = taskManager,
                            onBack = {
                                currentScreen = AppScreen.TASKS
                            }
                        )
                    }
                }
            }
        }
    }
}