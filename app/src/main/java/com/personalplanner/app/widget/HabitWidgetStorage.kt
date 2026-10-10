package com.personalplanner.app.widget

import android.content.Context

class HabitWidgetStorage(context: Context) {

    private val preferences = context.getSharedPreferences(
        "habit_widget",
        Context.MODE_PRIVATE
    )

    fun saveHabitId(
        appWidgetId: Int,
        habitId: Long
    ) {
        preferences.edit()
            .putLong(
                "habit_$appWidgetId",
                habitId
            )
            .apply()
    }

    fun getHabitId(
        appWidgetId: Int
    ): Long? {
        val key = "habit_$appWidgetId"

        if (!preferences.contains(key)) {
            return null
        }

        return preferences.getLong(key, -1L)
    }

    fun deleteHabitId(
        appWidgetId: Int
    ) {
        preferences.edit()
            .remove("habit_$appWidgetId")
            .apply()
    }
}