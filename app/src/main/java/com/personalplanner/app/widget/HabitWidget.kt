package com.personalplanner.app.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.personalplanner.app.R
import com.personalplanner.app.manager.HabitManager

class HabitWidget : AppWidgetProvider() {
    override fun onReceive(
        context: Context,
        intent: android.content.Intent
    ) {
        super.onReceive(context, intent)

        if (intent.action == ACTION_TOGGLE_HABIT) {
            val appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )

            if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                return
            }

            val habitId = HabitWidgetStorage(context)
                .getHabitId(appWidgetId)
                ?: return

            com.personalplanner.app.manager.HabitManager
                .getInstance(context)
                .toggleToday(habitId)
        }
    }

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

        appWidgetIds.forEach { appWidgetId ->
            updateWidget(
                context,
                appWidgetManager,
                appWidgetId
            )
        }
    }

    companion object {
        const val ACTION_TOGGLE_HABIT =
            "com.personalplanner.app.widget.ACTION_TOGGLE_HABIT"

        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(
                context.packageName,
                R.layout.habit_widget
            )

            val habitId = HabitWidgetStorage(context)
                .getHabitId(appWidgetId)

            val habit = habitId?.let {
                HabitManager.getInstance(context)
                    .getHabits()
                    .firstOrNull { habit -> habit.id == it }
            }
            val isCompletedToday = habit?.let {
                HabitManager.getInstance(context)
                    .isCompletedToday(it.id)
            } ?: false
            val currentStreak = habit?.let {
                HabitManager.getInstance(context)
                    .getCurrentStreak(it.id)
            } ?: 0

            val isStreakBroken = habit?.let {
                val streaks = HabitManager.getInstance(context)
                    .getStreaks(it.id)

                if (streaks.isEmpty()) {
                    false
                } else {
                    val lastCompletedDate = streaks.first().second
                    val yesterday = java.time.LocalDate.now().minusDays(1)

                    !isCompletedToday &&
                            lastCompletedDate.isBefore(yesterday)
                }
            } ?: false

            views.setTextViewText(
                R.id.habitWidgetStreak,
                currentStreak.toString()
            )

            views.setTextViewText(
                R.id.habitWidgetTitle,
                habit?.name ?: "Привычка"
            )
            val backgroundResource = when {
                isCompletedToday -> R.drawable.corgi_cool
                isStreakBroken -> R.drawable.corgi_tired
                else -> R.drawable.corgi_sad
            }

            views.setImageViewResource(
                R.id.habitWidgetBackground,
                backgroundResource
            )
            views.setViewVisibility(
                R.id.habitWidgetFlameGray,
                if (isCompletedToday) {
                    android.view.View.GONE
                } else {
                    android.view.View.VISIBLE
                }
            )

            views.setViewVisibility(
                R.id.habitWidgetFlameActive,
                if (isCompletedToday) {
                    android.view.View.VISIBLE
                } else {
                    android.view.View.GONE
                }
            )

            views.setViewVisibility(
                R.id.habitWidgetStreak,
                android.view.View.VISIBLE
            )

            val toggleIntent = android.content.Intent(
                context,
                HabitWidget::class.java
            ).apply {
                action = ACTION_TOGGLE_HABIT
                putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    appWidgetId
                )
            }

            val togglePendingIntent =
                android.app.PendingIntent.getBroadcast(
                    context,
                    appWidgetId,
                    toggleIntent,
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT or
                            android.app.PendingIntent.FLAG_IMMUTABLE
                )

            views.setOnClickPendingIntent(
                R.id.habitWidgetFlame,
                togglePendingIntent
            )

            appWidgetManager.updateAppWidget(
                appWidgetId,
                views
            )
        }
        fun updateAll(context: Context) {
            val appWidgetManager =
                AppWidgetManager.getInstance(context)

            val componentName = android.content.ComponentName(
                context,
                HabitWidget::class.java
            )

            val appWidgetIds =
                appWidgetManager.getAppWidgetIds(componentName)

            appWidgetIds.forEach { appWidgetId ->
                updateWidget(
                    context,
                    appWidgetManager,
                    appWidgetId
                )
            }
        }
    }
}