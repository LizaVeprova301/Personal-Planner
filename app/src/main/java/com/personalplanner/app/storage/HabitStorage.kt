package com.personalplanner.app.storage

import android.content.Context
import com.personalplanner.app.model.Habit
import com.personalplanner.app.model.HabitCompletion
import org.json.JSONArray
import org.json.JSONObject

class HabitStorage(
    context: Context
) {

    private val preferences = context.getSharedPreferences(
        "habit_storage",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_HABITS = "habits"
    }

    fun saveHabits(habits: List<Habit>) {

        val jsonArray = JSONArray()

        habits.forEach { habit ->

            val jsonObject = JSONObject().apply {
                put("id", habit.id)
                put("name", habit.name)
                put("createdAt", habit.createdAt)
                put("deleted", habit.deleted)

                val completionsArray = JSONArray()

                habit.completions.forEach { completion ->
                    completionsArray.put(completion.date)
                }

                put("completions", completionsArray)
            }

            jsonArray.put(jsonObject)
        }

        preferences
            .edit()
            .putString(KEY_HABITS, jsonArray.toString())
            .apply()
    }

    fun loadHabits(): List<Habit> {

        val jsonString = preferences.getString(
            KEY_HABITS,
            null
        ) ?: return emptyList()

        val jsonArray = JSONArray(jsonString)

        val habits = mutableListOf<Habit>()

        for (i in 0 until jsonArray.length()) {

            val jsonObject = jsonArray.getJSONObject(i)

            val completions = mutableListOf<HabitCompletion>()

            val completionsArray =
                jsonObject.optJSONArray("completions")
                    ?: JSONArray()

            for (j in 0 until completionsArray.length()) {
                completions.add(
                    HabitCompletion(
                        date = completionsArray.getString(j)
                    )
                )
            }

            habits.add(
                Habit(
                    id = jsonObject.getLong("id"),
                    name = jsonObject.getString("name"),
                    createdAt = jsonObject.getLong("createdAt"),
                    deleted = jsonObject.optBoolean(
                        "deleted",
                        false
                    ),
                    completions = completions
                )
            )
        }

        return habits
    }
}