package com.personalplanner.app.storage

import android.content.Context
import com.personalplanner.app.model.Habit
import com.personalplanner.app.model.HabitCompletion
import org.json.JSONArray
import org.json.JSONObject

class HabitStorage(context: Context) {

    private val prefs = context.getSharedPreferences(
        "habit_storage",
        Context.MODE_PRIVATE
    )

    fun saveHabits(habits: List<Habit>) {
        val jsonArray = JSONArray()

        habits.forEach { habit ->
            val json = JSONObject().apply {
                put("id", habit.id)
                put("name", habit.name)
                put("createdAt", habit.createdAt)
                put("deleted", habit.deleted)

                val completionsArray = JSONArray()
                habit.completions.forEach { completion ->
                    completionsArray.put(
                        JSONObject().put("date", completion.date)
                    )
                }
                put("completions", completionsArray)
            }

            jsonArray.put(json)
        }

        prefs.edit()
            .putString("habits", jsonArray.toString())
            .apply()
    }

    fun loadHabits(): List<Habit> {
        val raw = prefs.getString("habits", null) ?: return emptyList()

        return runCatching {
            val jsonArray = JSONArray(raw)
            buildList {
                for (i in 0 until jsonArray.length()) {
                    val json = jsonArray.optJSONObject(i) ?: continue
                    val id = json.optLong("id", -1L)
                    val name = json.optString("name", "").trim()

                    if (id < 0L || name.isEmpty()) continue

                    val completionsArray =
                        json.optJSONArray("completions") ?: JSONArray()

                    val completions = buildList {
                        for (j in 0 until completionsArray.length()) {
                            val completion =
                                completionsArray.optJSONObject(j) ?: continue
                            val date = completion.optString("date", "")
                            if (date.isNotBlank()) {
                                add(HabitCompletion(date))
                            }
                        }
                    }

                    add(
                        Habit(
                            id = id,
                            name = name,
                            createdAt = json.optLong("createdAt", 0L),
                            deleted = json.optBoolean("deleted", false),
                            completions = completions
                        )
                    )
                }
            }
        }.getOrElse {
            emptyList()
        }
    }
}
