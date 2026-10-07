package com.personalplanner.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.personalplanner.app.ui.ArchiveScreen
import com.personalplanner.app.ui.TaskScreen
import com.personalplanner.app.ui.theme.PersonalPlannerTheme
import com.personalplanner.app.manager.TaskManager

enum class AppScreen {
    TASKS,
    ARCHIVE
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            PersonalPlannerTheme {

                val taskManager = remember {
                    TaskManager.getInstance(this@MainActivity)
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