
package com.personalplanner.app.storage

import android.content.Context

class MultiHabitWidgetStorage(context: Context) {

    private val preferences = context.getSharedPreferences(
        "multi_habit_widget",
        Context.MODE_PRIVATE
    )

    fun saveHabitIds(appWidgetId: Int, habitIds: List<Long>) {
        preferences.edit()
            .putString("habits_$appWidgetId", habitIds.joinToString(","))
            .apply()
    }

    fun getHabitIds(appWidgetId: Int): List<Long> {
        val saved = preferences.getString("habits_$appWidgetId", null)
            ?: return emptyList()

        return saved.split(",")
            .filter { it.isNotBlank() }
            .mapNotNull { it.toLongOrNull() }
    }

    fun deleteHabitIds(appWidgetId: Int) {
        preferences.edit()
            .remove("habits_$appWidgetId")
            .apply()
    }
    fun removeHabitId(appWidgetId: Int, habitId: Long) {
        val remainingIds = getHabitIds(appWidgetId)
            .filter { it != habitId }

        if (remainingIds.isEmpty()) {
            deleteHabitIds(appWidgetId)
        } else {
            saveHabitIds(appWidgetId, remainingIds)
        }
    }
}
