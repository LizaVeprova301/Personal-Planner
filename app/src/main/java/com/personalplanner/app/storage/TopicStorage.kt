package com.personalplanner.app.storage

import android.content.Context
import com.personalplanner.app.model.Topic
import org.json.JSONArray
import org.json.JSONObject

class TopicStorage(context: Context) {

    private val preferences = context.getSharedPreferences(
        "topic_storage",
        Context.MODE_PRIVATE
    )

    fun saveTopics(topics: List<Topic>) {
        val jsonArray = JSONArray()

        topics.forEach { topic ->
            val jsonObject = JSONObject().apply {
                put("id", topic.id)
                put("name", topic.name)
                put("color", topic.color)
            }
            jsonArray.put(jsonObject)
        }

        preferences.edit()
            .putString("topics", jsonArray.toString())
            .commit()
    }

    fun loadTopics(): List<Topic> {
        val json = preferences.getString("topics", null)
            ?: return emptyList()

        val jsonArray = JSONArray(json)
        val topics = mutableListOf<Topic>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            topics.add(
                Topic(
                    id = jsonObject.getLong("id"),
                    name = jsonObject.getString("name"),
                    color = jsonObject.getLong("color")
                )
            )
        }

        return topics
    }
}