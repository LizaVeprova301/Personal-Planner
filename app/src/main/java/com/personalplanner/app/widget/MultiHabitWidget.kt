
package com.personalplanner.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import com.personalplanner.app.R
import com.personalplanner.app.manager.HabitManager
import com.personalplanner.app.storage.MultiHabitWidgetStorage
import java.time.LocalDate

class MultiHabitWidget : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == ACTION_TOGGLE_HABIT) {
            val appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            val habitId = intent.getLongExtra(EXTRA_HABIT_ID, -1L)

            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID &&
                habitId != -1L
            ) {
                HabitManager.getInstance(context).toggleToday(habitId)
                HabitWidget.updateAll(context)
                updateAll(context)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { id ->
            updateWidget(context, appWidgetManager, id)
        }
    }

    companion object {
        const val ACTION_TOGGLE_HABIT =
            "com.personalplanner.app.widget.ACTION_TOGGLE_MULTI_HABIT"
        const val EXTRA_HABIT_ID = "multi_habit_id"

        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(
                context.packageName,
                R.layout.multi_habit_widget
            )

            val manager = HabitManager.getInstance(context)
            val selectedIds = MultiHabitWidgetStorage(context)
                .getHabitIds(appWidgetId)

            val habitsById = manager.getHabits().associateBy { it.id }

            val selectedHabits = selectedIds.mapNotNull { id ->
                habitsById[id]
            }

            val completedCount = selectedHabits.count {
                manager.isCompletedToday(it.id)
            }

            val allCompleted = selectedHabits.isNotEmpty() &&
                    completedCount == selectedHabits.size

            views.removeAllViews(R.id.multiHabitRow1)
            views.removeAllViews(R.id.multiHabitRow2)

            views.setViewVisibility(
                R.id.multiHabitFlameHappy,
                if (allCompleted) android.view.View.VISIBLE else android.view.View.GONE
            )
            views.setViewVisibility(
                R.id.multiHabitFlameSad,
                if (allCompleted) android.view.View.GONE else android.view.View.VISIBLE
            )

            views.setViewVisibility(
                R.id.multiHabitList,
                if (allCompleted) android.view.View.GONE else android.view.View.VISIBLE
            )


            val firstRowCount = when (selectedHabits.size) {
                4 -> 2
                5 -> 3
                else -> selectedHabits.size
            }

            selectedHabits.forEachIndexed { index, habit ->
                val completed = manager.isCompletedToday(habit.id)
                val item = RemoteViews(
                    context.packageName,
                    R.layout.multi_habit_item
                )

                item.setTextViewText(
                    R.id.multiHabitItemText,
                    habit.name
                )
                item.setInt(
                    R.id.multiHabitItemText,
                    "setBackgroundResource",
                    if (completed) {
                        R.drawable.multi_habit_item_completed
                    } else {
                        R.drawable.multi_habit_item_background
                    }
                )



                item.setTextColor(
                    R.id.multiHabitItemText,
                    Color.BLACK
                )

                val toggleIntent = Intent(
                    context,
                    MultiHabitWidget::class.java
                ).apply {
                    action = ACTION_TOGGLE_HABIT
                    putExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        appWidgetId
                    )
                    putExtra(EXTRA_HABIT_ID, habit.id)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    appWidgetId * 1000 + habit.id.toInt(),
                    toggleIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or
                            PendingIntent.FLAG_IMMUTABLE
                )

                item.setOnClickPendingIntent(
                    R.id.multiHabitItemText,
                    pendingIntent
                )

                val rowId = if (index < firstRowCount) {
                    R.id.multiHabitRow1
                } else {
                    R.id.multiHabitRow2
                }

                views.addView(rowId, item)
            }


            val groupStreak = getGroupStreak(
                manager,
                selectedHabits.map { it.id }
            ).toString()

            views.setTextViewText(R.id.multiHabitStreak, groupStreak)
            views.setTextViewText(R.id.multiHabitStreakSad, groupStreak)


            views.setImageViewResource(
                R.id.multiHabitCorgi,
                if (allCompleted) {
                    R.drawable.scroodge
                } else {
                    R.drawable.scroodge_sad
                }
            )

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }


        private fun getGroupStreak(
            manager: HabitManager,
            habitIds: List<Long>
        ): Int {
            if (habitIds.isEmpty()) return 0

            val streakRanges = habitIds.map { id ->
                manager.getStreaks(id)
            }

            val allDoneToday = habitIds.all { id ->
                manager.isCompletedToday(id)
            }

            var date = LocalDate.now()
            if (!allDoneToday) {
                date = date.minusDays(1)
            }

            var streak = 0

            while (streakRanges.all { ranges ->
                    ranges.any { range ->
                        date >= range.first && date <= range.second
                    }
                }
            ) {
                streak++
                date = date.minusDays(1)
            }

            return streak
        }

        fun removeHabitFromAllWidgets(context: Context, habitId: Long) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val component = ComponentName(context, MultiHabitWidget::class.java)

            val widgetIds = appWidgetManager.getAppWidgetIds(component)
            val storage = MultiHabitWidgetStorage(context)

            widgetIds.forEach { widgetId ->
                storage.removeHabitId(widgetId, habitId)
                updateWidget(context, appWidgetManager, widgetId)
            }
        }



        fun updateAll(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val component = ComponentName(
                context,
                MultiHabitWidget::class.java
            )

            appWidgetManager.getAppWidgetIds(component).forEach { id ->
                updateWidget(context, appWidgetManager, id)
            }
        }
    }
}
