package com.personalplanner.app.storage

import android.content.Context
import com.personalplanner.app.model.Task
import org.json.JSONArray
import org.json.JSONObject

class TaskStorage(
    context: Context
) {

    private val preferences = context.getSharedPreferences(
        "task_storage",
        Context.MODE_PRIVATE
    )

    fun saveTasks(tasks: List<Task>) {
        val jsonArray = JSONArray()

        tasks.forEach { task ->
            val jsonObject = JSONObject().apply {
                put("id", task.id)
                put("text", task.text)
                put("completed", task.completed)
                put("completedAt", task.completedAt)
                put("deleted", task.deleted)
                put("deletedFromArchive", task.deletedFromArchive)
                put("topicId", task.topicId)
            }

            jsonArray.put(jsonObject)
        }

        preferences.edit()
            .putString("tasks", jsonArray.toString())
            .apply()
    }


    fun loadTasks(): List<Task> {
        val json = preferences.getString("tasks", null)
            ?: return emptyList()

        return runCatching {
            val jsonArray = JSONArray(json)

            buildList {
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.optJSONObject(i) ?: continue

                    val id = jsonObject.optLong("id", -1L)
                    val text = jsonObject.optString("text", "").trim()

                    if (id < 0L || text.isEmpty()) continue

                    add(
                        Task(
                            id = id,
                            text = text,
                            completed = jsonObject.optBoolean("completed", false),
                            completedAt = if (
                                jsonObject.has("completedAt") &&
                                !jsonObject.isNull("completedAt")
                            ) {
                                jsonObject.optLong("completedAt")
                            } else {
                                null
                            },
                            deleted = jsonObject.optBoolean("deleted", false),
                            deletedFromArchive = jsonObject.optBoolean(
                                "deletedFromArchive",
                                false
                            ),
                            topicId = if (
                                jsonObject.has("topicId") &&
                                !jsonObject.isNull("topicId")
                            ) {
                                jsonObject.optLong("topicId")
                            } else {
                                null
                            }
                        )
                    )
                }
            }
        }.getOrElse {
            emptyList()
        }
    }

    fun hasSavedTasks(): Boolean {
        return preferences.contains("tasks")
    }

}