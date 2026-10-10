
package com.personalplanner.app.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.personalplanner.app.manager.HabitManager
import com.personalplanner.app.storage.MultiHabitWidgetStorage

class MultiHabitWidgetConfigActivity : Activity() {

    private val checkBoxes = mutableMapOf<Long, CheckBox>()
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setResult(RESULT_CANCELED)

        appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }

        val title = TextView(this).apply {
            text = "Выберите привычки (до 5)"
            textSize = 20f
        }
        root.addView(title)

        val scrollView = ScrollView(this)
        val habitsContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val habits = HabitManager.getInstance(this).getHabits()
        val savedIds = MultiHabitWidgetStorage(this)
            .getHabitIds(appWidgetId)
            .toSet()

        habits.forEach { habit ->
            val checkBox = CheckBox(this).apply {
                text = habit.name
                textSize = 16f
                isChecked = habit.id in savedIds
            }

            checkBox.setOnCheckedChangeListener { button, isChecked ->
                val selectedCount = checkBoxes.values.count { it.isChecked }

                if (isChecked && selectedCount > 5) {
                    button.isChecked = false
                    Toast.makeText(
                        this,
                        "Можно выбрать не больше 5 привычек",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            checkBoxes[habit.id] = checkBox
            habitsContainer.addView(checkBox)
        }

        scrollView.addView(habitsContainer)

        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        val saveButton = Button(this).apply {
            text = "Сохранить"
        }

        saveButton.setOnClickListener {
            val selectedIds = checkBoxes
                .filterValues { it.isChecked }
                .keys
                .toList()

            if (selectedIds.isEmpty()) {
                Toast.makeText(
                    this,
                    "Выбери хотя бы одну привычку",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }


            MultiHabitWidgetStorage(this)
                .saveHabitIds(appWidgetId, selectedIds)

            MultiHabitWidget.updateWidget(
                this,
                AppWidgetManager.getInstance(this),
                appWidgetId
            )

            val result = Intent().putExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                appWidgetId
            )


            setResult(RESULT_OK, result)
            finish()
        }

        root.addView(saveButton)

        setContentView(root)
    }
}
