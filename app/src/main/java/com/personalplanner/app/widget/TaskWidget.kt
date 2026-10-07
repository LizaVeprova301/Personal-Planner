package com.personalplanner.app.widget

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Column
import androidx.glance.layout.padding
import androidx.glance.text.Text
import com.personalplanner.app.storage.TaskStorage

class TaskWidget : GlanceAppWidget() {

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        println("WIDGET DEBUG: provideGlance ВЫЗВАН")
        provideContent {

            val storage = TaskStorage(context)
            val tasks = storage.loadTasks()
                .filter { !it.deleted }

            println("WIDGET DEBUG: задач в storage = ${tasks.size}")
            tasks.forEach {
                println("WIDGET DEBUG: задача = '${it.text}', deleted = ${it.deleted}")
            }

            Column(
                modifier = GlanceModifier
                    .padding(16.dp)
            ) {

                Text(
                    text = "Задачи"
                )

                tasks.forEach { task ->
                    Text(
                        text = "☐ ${task.text}"
                    )
                }
            }
        }
    }
}