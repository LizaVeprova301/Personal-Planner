package com.personalplanner.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.personalplanner.app.core.AppInitializer
import com.personalplanner.app.core.AppLifecycleObserver
import com.personalplanner.app.core.AppManagers
import com.personalplanner.app.ui.ArchiveScreen
import com.personalplanner.app.ui.HabitScreen
import com.personalplanner.app.ui.TaskScreen
import com.personalplanner.app.ui.theme.PersonalPlannerTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        val appManagers = AppManagers(this)

        val taskManager =
            appManagers.taskManager

        val habitManager =
            appManagers.habitManager

        lifecycle.addObserver(
            AppLifecycleObserver(taskManager)
        )
        AppInitializer.initialize(this)

        val openAddTask =
            intent.getBooleanExtra(
                "open_add_task",
                false
            )

        setContent {
            PersonalPlannerTheme {

                var currentScreen by remember {
                    mutableStateOf(AppScreen.TASKS)
                }

                when (currentScreen) {

                    AppScreen.TASKS -> {
                        TaskScreen(
                            taskManager = taskManager,
                            onOpenArchive = {
                                currentScreen = AppScreen.ARCHIVE
                            },
                            onOpenHabits = {
                                currentScreen = AppScreen.HABITS
                            },
                            openAddTask = openAddTask
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

                    AppScreen.HABITS -> {
                        HabitScreen(
                            habitManager = habitManager,
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