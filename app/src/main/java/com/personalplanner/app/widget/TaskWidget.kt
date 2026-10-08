package com.personalplanner.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.personalplanner.app.R
import com.personalplanner.app.storage.TaskStorage

class TaskWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(
            context,
            appWidgetManager,
            appWidgetIds
        )

        updateWidgets(
            context,
            appWidgetManager,
            appWidgetIds
        )
    }

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        super.onReceive(context, intent)

        if (intent.action == ACTION_TOGGLE_TASK) {

            val taskId = intent.getLongExtra(
                EXTRA_TASK_ID,
                -1L
            )

            if (taskId == -1L) {
                return
            }

            val storage = TaskStorage(context)
            val tasks = storage.loadTasks().toMutableList()

            val index = tasks.indexOfFirst {
                it.id == taskId
            }

            if (index != -1) {
                val task = tasks[index]

                tasks[index] = task.copy(
                    completed = !task.completed,
                    completedAt = if (!task.completed) {
                        System.currentTimeMillis()
                    } else {
                        null
                    }
                )

                storage.saveTasks(tasks)

                notifyListChanged(context)
            }
        }
    }

    companion object {

        const val ACTION_TOGGLE_TASK =
            "com.personalplanner.app.widget.ACTION_TOGGLE_TASK"

        const val EXTRA_TASK_ID = "task_id"

        fun updateAll(context: Context) {

            val appWidgetManager =
                AppWidgetManager.getInstance(context)

            val componentName = ComponentName(
                context,
                TaskWidget::class.java
            )

            val appWidgetIds =
                appWidgetManager.getAppWidgetIds(
                    componentName
                )

            if (appWidgetIds.isEmpty()) {
                return
            }

            updateWidgets(
                context,
                appWidgetManager,
                appWidgetIds
            )
        }
        private fun notifyListChanged(context: Context) {
            val appWidgetManager =
                AppWidgetManager.getInstance(context)

            val componentName = ComponentName(
                context,
                TaskWidget::class.java
            )

            val appWidgetIds =
                appWidgetManager.getAppWidgetIds(componentName)

            appWidgetIds.forEach { appWidgetId ->
                appWidgetManager.notifyAppWidgetViewDataChanged(
                    appWidgetId,
                    R.id.widgetTasksList
                )
            }
        }

        private fun updateWidgets(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
        ) {

            appWidgetIds.forEach { appWidgetId ->

                val views = RemoteViews(
                    context.packageName,
                    R.layout.task_widget
                )

                views.setTextViewText(
                    R.id.widgetTitle,
                    "Задачи"
                )

                val serviceIntent = Intent(
                    context,
                    TaskWidgetService::class.java
                ).apply {
                    putExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        appWidgetId
                    )
                }

                views.setRemoteAdapter(
                    appWidgetId,
                    R.id.widgetTasksList,
                    serviceIntent
                )

                val clickIntent = Intent(
                    context,
                    TaskWidget::class.java
                ).apply {
                    action = ACTION_TOGGLE_TASK
                }

                val clickPendingIntent =
                    PendingIntent.getBroadcast(
                        context,
                        appWidgetId,
                        clickIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or
                                PendingIntent.FLAG_MUTABLE
                    )

                views.setPendingIntentTemplate(
                    R.id.widgetTasksList,
                    clickPendingIntent
                )

                appWidgetManager.updateAppWidget(
                    appWidgetId,
                    views
                )

                appWidgetManager.notifyAppWidgetViewDataChanged(
                    appWidgetId,
                    R.id.widgetTasksList
                )
            }
        }
    }
}