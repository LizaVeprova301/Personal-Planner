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
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.personalplanner.app.ui.ArchiveScreen
import com.personalplanner.app.ui.TaskScreen
import com.personalplanner.app.ui.theme.PersonalPlannerTheme
import com.personalplanner.app.manager.TaskManager
import com.personalplanner.app.widget.TaskWidget
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

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
        updateWidgetAsync()
    }
    private fun updateWidgetAsync() {
        lifecycleScope.launch {
            try {
                TaskWidget().updateAll(this@MainActivity)
            } catch (e: Exception) {
                println("WIDGET DEBUG: Ошибка обновления виджета: ${e.message}")
            }
        }
    }
}