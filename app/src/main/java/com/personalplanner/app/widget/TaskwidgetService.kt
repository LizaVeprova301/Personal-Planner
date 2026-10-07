package com.personalplanner.app.widget

import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.personalplanner.app.R
import com.personalplanner.app.storage.TaskStorage

class TaskWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(
        intent: Intent
    ): RemoteViewsFactory {
        return TaskWidgetFactory(applicationContext)
    }
}

class TaskWidgetFactory(
    private val context: android.content.Context
) : RemoteViewsService.RemoteViewsFactory {

    private var tasks = emptyList<com.personalplanner.app.model.Task>()

    override fun onCreate() {
        loadTasks()
    }

    override fun onDataSetChanged() {
        loadTasks()
    }

    private fun loadTasks() {
        tasks = TaskStorage(context)
            .loadTasks()
            .filter { !it.deleted }
    }

    override fun getCount(): Int {
        return tasks.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val task = tasks[position]

        val views = RemoteViews(
            context.packageName,
            R.layout.task_widget_item
        )

        views.setTextViewText(
            R.id.widgetCheckbox,
            if (task.completed) "☑" else "☐"
        )

        views.setTextViewText(
            R.id.widgetTaskText,
            task.text
        )

        if (task.completed) {
            views.setInt(
                R.id.widgetTaskText,
                "setPaintFlags",
                android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            )
        }

        val intent = Intent().apply {
            putExtra(
                TaskWidget.EXTRA_TASK_ID,
                task.id
            )
        }

        views.setOnClickFillInIntent(
            R.id.widgetTaskRow,
            intent
        )

        return views
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return tasks[position].id
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun onDestroy() {
        tasks = emptyList()
    }
}