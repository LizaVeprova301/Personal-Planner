package com.personalplanner.app.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import com.personalplanner.app.R
import com.personalplanner.app.storage.TaskStorage

class TaskWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        if (appWidgetIds.isNotEmpty()) {
            updateWidgets(
                context,
                appWidgetManager,
                appWidgetIds
            )
        }
    }

    companion object {

        fun updateAll(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)

            val componentName = ComponentName(
                context,
                TaskWidget::class.java
            )

            val appWidgetIds =
                appWidgetManager.getAppWidgetIds(componentName)

            if (appWidgetIds.isEmpty()) {
                return
            }

            updateWidgets(
                context,
                appWidgetManager,
                appWidgetIds
            )
        }

        private fun updateWidgets(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
        ) {
            val tasks = TaskStorage(context)
                .loadTasks()
                .filter { !it.deleted }

            val widgetText = if (tasks.isEmpty()) {
                "Нет задач"
            } else {
                tasks.joinToString("\n") { task ->
                    val checkbox = if (task.completed) "☑" else "☐"
                    "$checkbox ${task.text}"
                }
            }

            appWidgetIds.forEach { appWidgetId ->

                val views = RemoteViews(
                    context.packageName,
                    R.layout.task_widget
                ).apply {

                    setTextViewText(
                        R.id.widgetTitle,
                        "Задачи"
                    )

                    setTextViewText(
                        R.id.widgetTasks,
                        widgetText
                    )
                }

                appWidgetManager.updateAppWidget(
                    appWidgetId,
                    views
                )
            }
        }
    }
}