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
                put("deleted", task.deleted)
                put("deletedFromArchive", task.deletedFromArchive)
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

        val jsonArray = JSONArray(json)
        val tasks = mutableListOf<Task>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)

            tasks.add(
                Task(
                    id = jsonObject.getLong("id"),
                    text = jsonObject.getString("text"),
                    completed = jsonObject.getBoolean("completed"),
                    deleted = jsonObject.getBoolean("deleted"),
                    deletedFromArchive = jsonObject.getBoolean("deletedFromArchive")
                )
            )
        }

        return tasks
    }
}