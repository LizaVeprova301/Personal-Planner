package com.personalplanner.app.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import com.personalplanner.app.manager.HabitManager

class HabitWidgetConfigActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )

        setResult(RESULT_CANCELED)

        val habitManager = HabitManager.getInstance(this)
        val habits = habitManager.getHabits()
        android.util.Log.d(
            "HabitWidgetConfig",
            "Habits count: ${habits.size}"
        )

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val title = TextView(this).apply {
            text = "Выберите привычку"
            textSize = 22f
        }

        layout.addView(title)

        habits.forEach { habit ->
            val habitView = TextView(this).apply {
                text = habit.name
                textSize = 18f
                setPadding(0, 24, 0, 24)

                setOnClickListener {
                    HabitWidgetStorage(this@HabitWidgetConfigActivity)
                        .saveHabitId(
                            appWidgetId = appWidgetId,
                            habitId = habit.id
                        )

                    HabitWidget.updateWidget(
                        this@HabitWidgetConfigActivity,
                        AppWidgetManager.getInstance(this@HabitWidgetConfigActivity),
                        appWidgetId
                    )

                    setResult(
                        RESULT_OK,
                        intent.putExtra(
                            AppWidgetManager.EXTRA_APPWIDGET_ID,
                            appWidgetId
                        )
                    )

                    finish()
                }
            }

            layout.addView(habitView)
        }

        setContentView(layout)
    }
}