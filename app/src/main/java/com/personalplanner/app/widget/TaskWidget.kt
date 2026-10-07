package com.personalplanner.app.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.graphics.Paint
import android.util.TypedValue
import android.widget.RemoteViews
import com.personalplanner.app.R
import com.personalplanner.app.storage.TaskStorage
import android.app.PendingIntent
import android.content.Intent

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
                    completed = !task.completed
                )

                storage.saveTasks(tasks)

                updateAll(context)
            }
        }
    }

    companion object {

        const val ACTION_TOGGLE_TASK =
            "com.personalplanner.app.widget.ACTION_TOGGLE_TASK"

        const val EXTRA_TASK_ID = "task_id"

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

            appWidgetIds.forEach { appWidgetId ->

                val views = RemoteViews(
                    context.packageName,
                    R.layout.task_widget
                )

                views.setTextViewText(
                    R.id.widgetTitle,
                    "Задачи"
                )

                views.removeAllViews(
                    R.id.widgetTasksContainer
                )

                if (tasks.isEmpty()) {

                    val emptyView = RemoteViews(
                        context.packageName,
                        android.R.layout.simple_list_item_1
                    )

                    emptyView.setTextViewText(
                        android.R.id.text1,
                        "Нет задач"
                    )

                    views.addView(
                        R.id.widgetTasksContainer,
                        emptyView
                    )

                } else {

                    tasks.forEach { task ->

                        val taskView = RemoteViews(
                            context.packageName,
                            android.R.layout.simple_list_item_1
                        )
                        val intent = Intent(
                            context,
                            TaskWidget::class.java
                        ).apply {
                            action = ACTION_TOGGLE_TASK
                            putExtra(EXTRA_TASK_ID, task.id)
                        }

                        val pendingIntent = PendingIntent.getBroadcast(
                            context,
                            task.id.toInt(),
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )

                        taskView.setOnClickPendingIntent(
                            android.R.id.text1,
                            pendingIntent
                        )

                        val checkbox =
                            if (task.completed) "☑" else "☐"

                        taskView.setTextViewText(
                            android.R.id.text1,
                            "$checkbox ${task.text}"
                        )

                        taskView.setTextColor(
                            android.R.id.text1,
                            android.graphics.Color.WHITE
                        )

                        taskView.setTextViewTextSize(
                            android.R.id.text1,
                            TypedValue.COMPLEX_UNIT_SP,
                            14f
                        )

                        if (task.completed) {
                            taskView.setInt(
                                android.R.id.text1,
                                "setPaintFlags",
                                Paint.STRIKE_THRU_TEXT_FLAG
                            )
                        }

                        views.addView(
                            R.id.widgetTasksContainer,
                            taskView
                        )
                    }
                }

                appWidgetManager.updateAppWidget(
                    appWidgetId,
                    views
                )
            }
        }
    }
}